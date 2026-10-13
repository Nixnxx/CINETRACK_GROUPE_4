package controleur;

import modele.Film;
import dao.FilmDAO;
import dao.LogDAO;
import exceptions.FilmIntrouvableException;
import exceptions.NoteInvalideException;
import view.FenetreFilms;

import java.util.List;

public class FilmControleur {
    private FenetreFilms vue;
    private FilmDAO dao;
    private LogDAO logDao;

    public FilmControleur(FenetreFilms vue) {
        this.vue = vue;
        this.dao = new FilmDAO();
        this.logDao = new LogDAO();
    }

    public void chargerFilms() {
        List<Film> films = dao.findAll();
        vue.afficherFilms(films);
    }

    public void ajouterFilm(String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);
            Film film = new Film(titre, genre, duree, note, synopsis);
            dao.insert(film);

            // Log Java (le trigger PostgreSQL log aussi côté BD)
            logDao.logAjoutFilm(film.getIdFilm(), titre);

            vue.afficherSucces("Film ajouté avec succès !");
            chargerFilms();
        } catch (NumberFormatException e) {
            vue.afficherErreur("Durée ou note invalide (format numérique requis).");
        } catch (NoteInvalideException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void modifierFilm(int id, String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            // Récupérer l'ancien titre avant modification pour le log
            Film ancien = dao.findById(id);
            String ancienTitre = (ancien != null) ? ancien.getTitre() : "inconnu";

            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);
            Film film = new Film(id, titre, genre, duree, note, synopsis);
            dao.update(film);

            // Log Java
            logDao.logModificationFilm(id, ancienTitre, titre);

            vue.afficherSucces("Film modifié avec succès !");
            chargerFilms();
        } catch (NumberFormatException e) {
            vue.afficherErreur("Durée ou note invalide.");
        } catch (FilmIntrouvableException | NoteInvalideException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void supprimerFilm(int id, String titre) {
        boolean confirmation = vue.demanderConfirmation("Supprimer le film \"" + titre + "\" ?");
        if (confirmation) {
            try {
                dao.delete(id);

                // Log Java
                logDao.logSuppressionFilm(id, titre);

                vue.afficherSucces("Film supprimé !");
                chargerFilms();
            } catch (FilmIntrouvableException e) {
                vue.afficherErreur(e.getMessage());
            }
        }
    }

    public void rechercherParGenre(String genre) {
        if (genre == null || genre.isEmpty() || "Tous".equals(genre)) {
            chargerFilms();
        } else {
            // Log de la recherche
            logDao.logRecherche("film", "genre=" + genre);

            List<Film> films = dao.findByGenre(genre);
            vue.afficherFilms(films);
        }
    }

    public Film getFilmParId(int id) {
        return dao.findById(id);
    }
}
