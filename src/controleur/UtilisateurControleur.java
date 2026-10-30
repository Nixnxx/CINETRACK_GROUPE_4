package controleur;

import dao.LogDAO;
import dao.UtilisateurDAO;
import modele.Utilisateur;

import java.sql.SQLException;

/*
  UtilisateurControleur – règles métier de l'authentification et de la gestion des comptes.
    • new UtilisateurControleur()      → utilisé par DialogueLogin (connexion)
    • new UtilisateurControleur(vue)   → utilisé par FenetreUtilisateurs (admin uniquement)
 */
public class UtilisateurControleur {

    // Utilisateur actuellement connecté (session de l'application)
    private static Utilisateur utilisateurCourant;

    private final UtilisateurVue vue;
    private final UtilisateurDAO      dao    = new UtilisateurDAO();
    private final LogDAO              logDao = new LogDAO();

    public UtilisateurControleur() {
        this(null);
    }

    public UtilisateurControleur(UtilisateurVue vue) {
        this.vue = vue;
    }

    public static Utilisateur getUtilisateurCourant() {
        return utilisateurCourant;
    }

    public static boolean estAdminConnecte() {
        return utilisateurCourant != null && utilisateurCourant.estAdmin();
    }

    // ── Connexion ─────────────────────────────────────────────

    /** @return null si la connexion réussit, sinon le message d'erreur à afficher. */
    public String connecter(String login, String motDePasse) {
        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isEmpty()) {
            return "Identifiant et mot de passe requis.";
        }
        try {
            Utilisateur u = dao.authentifier(login.trim(), motDePasse);
            if (u == null) {
                return "Identifiant ou mot de passe incorrect.";
            }
            utilisateurCourant = u;
            LogDAO.setRoleActuel(u.getRole());   // le rôle doit être défini avant le log
            logDao.logConnexion(u.getLogin());
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Base de données inaccessible.";
        }
    }

    // ── Gestion des gestionnaires (admin uniquement) ──────────

    public void chargerGestionnaires() {
        if (!verifierAdmin()) return;
        try {
            vue.afficherGestionnaires(dao.findGestionnaires());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void ajouterGestionnaire(String login, String motDePasse, String confirmation) {
        if (!verifierAdmin()) return;

        String l = (login == null) ? "" : login.trim();
        if (!l.matches("[A-Za-z0-9_.-]{3,30}")) {
            vue.afficherErreur("Identifiant invalide : 3 à 30 caractères "
                    + "(lettres, chiffres, point, tiret, underscore).");
            return;
        }
        if (motDePasse == null || motDePasse.length() < 6) {
            vue.afficherErreur("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        if (!motDePasse.equals(confirmation)) {
            vue.afficherErreur("Les deux mots de passe ne correspondent pas.");
            return;
        }

        try {
            if (dao.loginExiste(l)) {
                vue.afficherErreur("Cet identifiant existe déjà.");
                return;
            }
            int id = dao.insererGestionnaire(l, motDePasse);

            // Log Java (le trigger PostgreSQL log aussi côté BD)
            logDao.logAjoutUtilisateur(id, l);

            vue.afficherSucces("Gestionnaire \"" + l + "\" créé avec succès !");
            vue.viderFormulaire();
            chargerGestionnaires();
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void supprimerGestionnaire(int idUtilisateur, String login) {
        if (!verifierAdmin()) return;

        if (!vue.demanderConfirmation("Supprimer le gestionnaire \"" + login + "\" ?")) return;

        try {
            if (dao.supprimerGestionnaire(idUtilisateur)) {
                // Log Java
                logDao.logSuppressionUtilisateur(idUtilisateur, login);

                vue.afficherSucces("Gestionnaire supprimé !");
                chargerGestionnaires();
            } else {
                vue.afficherErreur("Suppression impossible : compte introuvable ou non supprimable.");
            }
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    // ── Outils internes ───────────────────────────────────────

    /** Vérifie que l'utilisateur connecté est admin (sécurité côté contrôleur, en plus de l'interface). */
    private boolean verifierAdmin() {
        if (estAdminConnecte()) return true;
        if (vue != null) vue.afficherErreur("Action réservée à l'administrateur.");
        return false;
    }

    private void erreurBase(SQLException e) {
        e.printStackTrace();
        if (vue != null) vue.afficherErreur("Erreur base de données : " + e.getMessage());
    }
}
