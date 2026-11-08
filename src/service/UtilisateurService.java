package service;

import dao.LogDAO;
import dao.LogDAOImpl;
import dao.UtilisateurDAO;
import dao.UtilisateurDAOImpl;
import modele.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public class UtilisateurService {

    // Utilisateur actuellement connecté (session de l'application).
    // static : partagé par toutes les instances du service.
    private static Utilisateur utilisateurCourant;

    private final UtilisateurDAO dao = new UtilisateurDAOImpl();
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
        Utilisateur u = dao.authentifier(login.trim(), motDePasse);
        if (u == null) {
            return "Identifiant ou mot de passe incorrect.";
        }
        utilisateurCourant = u;
        LogDAO.setRoleActuel(u.getRole());   // le rôle doit être défini avant le log
        logDao.logConnexion(u.getLogin());
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
        return dao.findGestionnaires();
    }

    // Lance IllegalStateException (pas admin) ou IllegalArgumentException (données invalides)
    public void ajouterGestionnaire(String login, String motDePasse, String confirmation) throws SQLException {
        verifierAdmin();

        String l = (login == null) ? "" : login.trim();
        if (!l.matches("[A-Za-z0-9_.-]{3,30}")) {
            throw new IllegalArgumentException("Identifiant invalide : 3 à 30 caractères "
                    + "(lettres, chiffres, point, tiret, underscore).");
        }
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        if (!motDePasse.equals(confirmation)) {
            throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas.");
        }
        if (dao.loginExiste(l)) {
            throw new IllegalArgumentException("Cet identifiant existe déjà.");
        }

        int id = dao.insererGestionnaire(l, motDePasse);

        // Log Java (le trigger PostgreSQL log aussi côté BD)
        logDao.logAjoutUtilisateur(id, l);
    }

    // Lance IllegalStateException si l'utilisateur connecté n'est pas admin
    public boolean supprimerGestionnaire(int idUtilisateur, String login) throws SQLException {
        verifierAdmin();

        if (!dao.supprimerGestionnaire(idUtilisateur)) {
            return false;
        }
        logDao.logSuppressionUtilisateur(idUtilisateur, login);
        return true;
    }
}