package service;

import dao.FilmDAO;
import dao.FilmDAOImpl;
import dao.LogDAO;
import dao.LogDAOImpl;
import modele.Film;

import java.util.List;

public class FilmService {

    private final FilmDAO filmDAO;
    private final LogDAO logDAO;

    public FilmService() {
        this(new FilmDAOImpl(), new LogDAOImpl());
    }

    // Permet d'injecter des DAO InMemory pour les tests
    public FilmService(FilmDAO filmDAO, LogDAO logDAO) {
        this.filmDAO = filmDAO;
        this.logDAO = logDAO;
    }

    public List<Film> getTousLesFilms() {
        return filmDAO.findAll();
    }

    // Lance FilmIntrouvableException si l'id n'existe pas
    public Film getFilm(int id) {
        return filmDAO.findById(id);
    }

    public List<Film> rechercherParGenre(String genre) {
        if (genre == null || genre.isEmpty() || "Tous".equals(genre)) {
            return filmDAO.findAll();
        }
        logDAO.logRecherche("film", "genre=" + genre);
        return filmDAO.findByGenre(genre);
    }

    // Lance NoteInvalideException (via Film) ou IllegalArgumentException
    public boolean ajouterFilm(String titre, String genre, int duree, double note, String synopsis) {
        verifierTitreEtDuree(titre, duree);
        Film film = new Film(titre.trim(), genre, duree, note, synopsis);

        if (!filmDAO.insert(film)) {
            return false;
        }
        logDAO.logAjoutFilm(film.getIdFilm(), film.getTitre());
        return true;
    }

    // Lance FilmIntrouvableException, NoteInvalideException ou IllegalArgumentException
    public boolean modifierFilm(int id, String titre, String genre, int duree, double note, String synopsis) {
        verifierTitreEtDuree(titre, duree);
        Film ancien = filmDAO.findById(id);
        Film film = new Film(id, titre.trim(), genre, duree, note, synopsis);

        if (!filmDAO.update(film)) {
            return false;
        }
        logDAO.logModificationFilm(id, ancien.getTitre(), film.getTitre());
        return true;
    }

    // Lance FilmIntrouvableException si l'id n'existe pas
    public boolean supprimerFilm(int id) {
        Film film = filmDAO.findById(id);

        if (!filmDAO.delete(id)) {
            return false;
        }
        logDAO.logSuppressionFilm(id, film.getTitre());
        return true;
    }

    private void verifierTitreEtDuree(String titre, int duree) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre du film est obligatoire.");
        }
        if (duree <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0.");
        }
    }
}