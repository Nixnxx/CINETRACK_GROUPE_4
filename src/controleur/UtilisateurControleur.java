package controleur;

import modele.Utilisateur;
import service.UtilisateurService;

import java.sql.SQLException;

/*
  UtilisateurControleur
    • new UtilisateurControleur()      → connexion
    • new UtilisateurControleur(vue)   → gestion des gestionnaires (admin uniquement)
 */
public class UtilisateurControleur {

    private final UtilisateurVue vue;
    private final UtilisateurService service = new UtilisateurService();

    public UtilisateurControleur() {
        this(null);
    }

    public UtilisateurControleur(UtilisateurVue vue) {
        this.vue = vue;
    }

    // Gardées ici pour ne pas modifier les classes qui les appellent déjà
    public static Utilisateur getUtilisateurCourant() {
        return UtilisateurService.getUtilisateurCourant();
    }

    public static boolean estAdminConnecte() {
        return UtilisateurService.estAdminConnecte();
    }

    // ── Connexion ─────────────────────────────────────────────

    /** @return null si la connexion réussit, sinon le message d'erreur à afficher. */
    public String connecter(String login, String motDePasse) {
        try {
            return service.connecter(login, motDePasse);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Base de données inaccessible.";
        }
    }

    // ── Gestion des gestionnaires (admin uniquement) ──────────

    public void chargerGestionnaires() {
        try {
            vue.afficherGestionnaires(service.getGestionnaires());
        } catch (IllegalStateException e) {
            erreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void ajouterGestionnaire(String login, String motDePasse, String confirmation) {
        try {
            service.ajouterGestionnaire(login, motDePasse, confirmation);

            vue.afficherSucces("Gestionnaire \"" + login.trim() + "\" créé avec succès !");
            vue.viderFormulaire();
            chargerGestionnaires();
        } catch (IllegalArgumentException | IllegalStateException e) {
            erreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void supprimerGestionnaire(int idUtilisateur, String login) {
        try {
            service.verifierAdmin();   // avant de demander la confirmation

            if (!vue.demanderConfirmation("Supprimer le gestionnaire \"" + login + "\" ?")) {
                return;
            }
            if (service.supprimerGestionnaire(idUtilisateur, login)) {
                vue.afficherSucces("Gestionnaire supprimé !");
                chargerGestionnaires();
            } else {
                vue.afficherErreur("Suppression impossible : compte introuvable ou non supprimable.");
            }
        } catch (IllegalStateException e) {
            erreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    // ── Outils internes ───────────────────────────────────────

    private void erreur(String message) {
        if (vue != null) vue.afficherErreur(message);
    }

    private void erreurBase(SQLException e) {
        e.printStackTrace();
        erreur("Erreur base de données : " + e.getMessage());
    }
}