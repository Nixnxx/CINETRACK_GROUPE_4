package controleur;

import modele.Seance;

import java.util.List;

// Ce que le contrôleur attend d'une vue "Séances"
public interface SeanceVue {

    void afficherSeances(List<Seance> seances);

    void afficherSucces(String message);

    void afficherErreur(String message);
}