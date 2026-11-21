package service;

import dao.LogDAO;
import dao.LogDAOImpl;
import dao.UtilisateurDAO;
import dao.UtilisateurDAOImpl;
import model.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public class UtilisateurService {

    // Anti force brute : 5 mots de passe faux de suite => compte bloqué 15 minutes
    private static final int MAX_TENTATIVES = 5;
    private static final int DUREE_BLOCAGE_MINUTES = 15;

    // Utilisateur actuellement connecté (session de l'application).
    // static : partagé par toutes les instances du service.
    private static Utilisateur utilisateurCourant;

    private final UtilisateurDAO utilisateurDao = new UtilisateurDAOImpl();
    private final LogDAO logDao = new LogDAOImpl();

    public static Utilisateur getUtilisateurCourant() {
        return utilisateurCourant;
    }

    public static boolean estAdminConnecte() {
        return utilisateurCourant != null && utilisateurCourant.estAdmin();
    }

    // Retourne null si la connexion réussit, sinon le message d'erreur à afficher
    public String connecter(String login, String motDePasse) throws SQLException {
        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isEmpty()) {
            return "Identifiant et mot de passe requis.";
        }
        String loginNettoye = login.trim();

        // 1) Compte bloqué ? On refuse même si le mot de passe est correct.
        int minutes = utilisateurDao.minutesRestantesBlocage(loginNettoye);
        if (minutes > 0) {
            return "Compte bloqué suite à trop de tentatives. Réessayez dans "
                    + minutes + " minute(s).";
        }

        // 2) Vérification du mot de passe
        Utilisateur utilisateur = utilisateurDao.authentifier(loginNettoye, motDePasse);
        if (utilisateur == null) {
            int echecs = utilisateurDao.enregistrerEchec(loginNettoye);
            if (echecs >= MAX_TENTATIVES) {
                utilisateurDao.bloquer(loginNettoye, DUREE_BLOCAGE_MINUTES);
                return "Trop de tentatives : compte bloqué pendant "
                        + DUREE_BLOCAGE_MINUTES + " minutes.";
            }
            return "Identifiant ou mot de passe incorrect.";
        }

        // 3) Connexion réussie : le compteur d'échecs repart à 0
        utilisateurDao.reinitialiserTentatives(utilisateur.getLogin());
        utilisateurCourant = utilisateur;
        LogDAO.setRoleActuel(utilisateur.getRole());   // le rôle doit être défini avant le log
        logDao.logConnexion(utilisateur.getLogin());
        return null;
    }

    // Lance IllegalStateException si l'utilisateur connecté n'est pas admin
    public void verifierAdmin() {
        if (!estAdminConnecte()) {
            throw new IllegalStateException("Action réservée à l'administrateur.");
        }
    }

    public List<Utilisateur> getGestionnaires() throws SQLException {
        verifierAdmin();
        return utilisateurDao.findGestionnaires();
    }

    // Lance IllegalStateException (pas admin) ou IllegalArgumentException (données invalides)
    public void ajouterGestionnaire(String login, String motDePasse,
                                    String confirmationMotDePasse) throws SQLException {
        verifierAdmin();

        String loginNettoye = (login == null) ? "" : login.trim();
        if (!loginNettoye.matches("[A-Za-z0-9_.-]{3,30}")) {
            throw new IllegalArgumentException("Identifiant invalide : 3 à 30 caractères "
                    + "(lettres, chiffres, point, tiret, underscore).");
        }
        if (motDePasse == null || motDePasse.length() < 6) {
               throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        if (!motDePasse.equals(confirmationMotDePasse)) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas.");
        }
        if (utilisateurDao.loginExiste(loginNettoye)) {
            throw new IllegalArgumentException("Cet identifiant existe déjà.");
        }

        int idNouveauGestionnaire = utilisateurDao.insererGestionnaire(loginNettoye, motDePasse);

        // Log Java (le trigger PostgreSQL log aussi côté BD)
        logDao.logAjoutUtilisateur(idNouveauGestionnaire, loginNettoye);
    }

    // Lance IllegalStateException si l'utilisateur connecté n'est pas admin
    public boolean supprimerGestionnaire(int idUtilisateur, String login) throws SQLException {
        verifierAdmin();

        if (!utilisateurDao.supprimerGestionnaire(idUtilisateur)) {
            return false;
        }
        logDao.logSuppressionUtilisateur(idUtilisateur, login);
        return true;
    }
}
