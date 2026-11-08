package controleur;

import exceptions.FilmIntrouvableException;
import exceptions.NoteInvalideException;
import modele.Film;
import service.FilmService;

public class FilmControleur {
    private final FilmVue vue;
    private final FilmService service;

    public FilmControleur(FilmVue vue) {
        this.vue = vue;
        this.service = new FilmService();
    }

    public void chargerFilms() {
        vue.afficherFilms(service.getTousLesFilms());
    }

    public void ajouterFilm(String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);

            if (service.ajouterFilm(titre, genre, duree, note, synopsis)) {
                vue.afficherSucces("Film ajouté avec succès !");
                chargerFilms();
            } else {
                vue.afficherErreur("L'ajout du film a échoué.");
            }
        } catch (NumberFormatException e) {
            vue.afficherErreur("Durée ou note invalide (format numérique requis).");
        } catch (NoteInvalideException | IllegalArgumentException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void modifierFilm(int id, String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);

            if (service.modifierFilm(id, titre, genre, duree, note, synopsis)) {
                vue.afficherSucces("Film modifié avec succès !");
                chargerFilms();
            } else {
                vue.afficherErreur("La modification du film a échoué.");
            }
        } catch (NumberFormatException e) {
            vue.afficherErreur("Durée ou note invalide.");
        } catch (FilmIntrouvableException | NoteInvalideException | IllegalArgumentException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void supprimerFilm(int id, String titre) {
        if (vue.demanderConfirmation("Supprimer le film \"" + titre + "\" ?")) {
            try {
                if (service.supprimerFilm(id)) {
                    vue.afficherSucces("Film supprimé !");
                    chargerFilms();
                } else {
                    vue.afficherErreur("La suppression du film a échoué.");
                }
            } catch (FilmIntrouvableException e) {
                vue.afficherErreur(e.getMessage());
            }
        }
    }

    public void rechercherParGenre(String genre) {
        vue.afficherFilms(service.rechercherParGenre(genre));
    }

    public Film getFilmParId(int id) {
        return service.getFilm(id);
    }
}