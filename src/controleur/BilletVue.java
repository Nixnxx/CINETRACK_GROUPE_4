package controleur;

import modele.Place;
import modele.Seance;

import java.util.List;

// Ce que le contrôleur attend d'une vue "Billetterie"
public interface BilletVue {

    void afficherSeances(List<Seance> seances);

    // Affiche la grille des places d'une salle ; les id des places déjà vendues sont à griser
    void afficherPlaces(List<Place> places, List<Integer> placesVendues);

    void viderFormulaire();

    void afficherSucces(String message);

    void afficherErreur(String message);
}