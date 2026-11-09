package service;

import dao.LogDAO;
import dao.LogDAOImpl;
import dao.SalleDAO;
import dao.SalleDAOImpl;
import dao.SeanceDAO;
import dao.SeanceDAOImpl;
import model.Film;
import model.Salle;
import model.Seance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SeanceService {

    private final SeanceDAO seanceDAO;
    private final SalleDAO salleDAO;
    private final FilmService filmService;
    private final LogDAO logDAO;

    public SeanceService() {
        this(new SeanceDAOImpl(), new SalleDAOImpl(), new FilmService(), new LogDAOImpl());
    }

    public SeanceService(SeanceDAO seanceDAO, SalleDAO salleDAO, FilmService filmService, LogDAO logDAO) {
        this.seanceDAO = seanceDAO;
        this.salleDAO = salleDAO;
        this.filmService = filmService;
        this.logDAO = logDAO;
    }

    public List<Seance> getToutesSeances() {
        return seanceDAO.findAll();
    }

    // Lance IllegalArgumentException si la date n'est pas au format AAAA-MM-JJ
    public List<Seance> getSeancesParDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return seanceDAO.findAll();
        }
        LocalDate jour;
        try {
            jour = LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date invalide (format attendu : AAAA-MM-JJ).");
        }
        logDAO.logRecherche("seance", "date=" + jour);
        return seanceDAO.findByDate(jour.toString());
    }

    // Lance SalleIndisponibleException (via le DAO) ou IllegalArgumentException
    public boolean ajouterSeance(int idFilm, int idSalle, LocalDate date, LocalTime heure,
                                 int places, double prix) {
        if (prix <= 0) {
            throw new IllegalArgumentException("Le prix doit être positif.");
        }
        if (places <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit être supérieur à 0.");
        }

        Seance seance = new Seance(idFilm, idSalle, date, heure, places, prix);

        if (!seanceDAO.insert(seance)) {
            return false;
        }
        logDAO.logAjoutSeance(seance.getIdSeance(), idFilm, date.toString());
        return true;
    }

    // Lance IllegalStateException (via le DAO) si des billets existent pour la séance
    public boolean supprimerSeance(int idSeance) {
        if (!seanceDAO.delete(idSeance)) {
            return false;
        }
        logDAO.logSuppressionSeance(idSeance);
        return true;
    }

    public List<Film> getTousFilms() {
        return filmService.getTousLesFilms();
    }

    public List<Salle> getToutesSalles() {
        return salleDAO.findAll();
    }
}