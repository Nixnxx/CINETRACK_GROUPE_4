package controleur;

import modele.Film;

import java.util.List;

// Ce que le contrôleur attend d'une vue "Films" (Swing ou JavaFX)
public interface FilmVue {

    void afficherFilms(List<Film> films);

    void afficherSucces(String message);

    void afficherErreur(String message);

    boolean demanderConfirmation(String message);
}