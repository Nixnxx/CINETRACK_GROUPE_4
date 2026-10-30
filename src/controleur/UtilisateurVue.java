package controleur;

import modele.Utilisateur;

import java.util.List;

// Ce que le contrôleur attend d'une vue "Gestionnaires"
public interface UtilisateurVue {

    void afficherGestionnaires(List<Utilisateur> utilisateurs);

    void viderFormulaire();

    void afficherSucces(String message);

    void afficherErreur(String message);

    boolean demanderConfirmation(String message);
}