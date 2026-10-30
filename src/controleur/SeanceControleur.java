package controleur;

import modele.Seance;
import modele.Film;
import modele.Salle;
import dao.SeanceDAO;
import dao.FilmDAO;
import dao.SalleDAO;
import dao.LogDAO;
import exceptions.SalleIndisponibleException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SeanceControleur {
    private SeanceVue vue;
    private SeanceDAO dao;
    private FilmDAO filmDao;
    private SalleDAO salleDao;
    private LogDAO logDao;

    public SeanceControleur(SeanceVue vue) {
        this.vue = vue;
        this.dao = new SeanceDAO();
        this.filmDao = new FilmDAO();
        this.salleDao = new SalleDAO();
        this.logDao = new LogDAO();
    }

    public void chargerToutesSeances() {
        List<Seance> seances = dao.findAll();
        vue.afficherSeances(seances);
    }

    public void chargerSeancesParDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            chargerToutesSeances();
        } else {
            // Log de la recherche par date
            logDao.logRecherche("seance", "date=" + date);

            List<Seance> seances = dao.findByDate(date);
            vue.afficherSeances(seances);
        }
    }

    // MODIF : nouveau paramètre prixStr, et on vérifie le résultat de l'insertion
    public void ajouterSeance(int idFilm, int idSalle, String dateStr, String heureStr,
                              String placesStr, String prixStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heure = LocalTime.parse(heureStr);
            int places = Integer.parseInt(placesStr);
            double prix = Double.parseDouble(prixStr);

            if (prix <= 0) {
                vue.afficherErreur("Le prix doit être positif.");
                return;
            }

            Seance seance = new Seance(idFilm, idSalle, date, heure, places, prix);

            if (dao.insert(seance)) {
                // Log Java
                logDao.logAjoutSeance(seance.getIdSeance(), idFilm, dateStr);

                vue.afficherSucces("Séance ajoutée avec succès !");
                chargerToutesSeances();
            } else {
                vue.afficherErreur("Erreur lors de l'ajout de la séance.");
            }
        } catch (SalleIndisponibleException e) {
            vue.afficherErreur(e.getMessage());
        } catch (Exception e) {
            vue.afficherErreur("Données invalides (date YYYY-MM-DD, heure HH:MM:SS, places et prix numériques)");
        }
    }

    // MODIF : gestion du refus quand la séance a déjà des billets
    public void supprimerSeance(int idSeance) {
        try {
            boolean success = dao.delete(idSeance);
            if (success) {
                // Log Java
                logDao.logSuppressionSeance(idSeance);

                vue.afficherSucces("Séance supprimée !");
                chargerToutesSeances();
            } else {
                vue.afficherErreur("Erreur lors de la suppression");
            }
        } catch (IllegalStateException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public List<Film> getTousFilms() {
        return filmDao.findAll();
    }

    public List<Salle> getToutesSalles() {
        return salleDao.findAll();
    }

    public String[] getFilmsPourCombo() {
        List<Film> films = filmDao.findAll();
        String[] result = new String[films.size()];
        for (int i = 0; i < films.size(); i++) {
            result[i] = films.get(i).getIdFilm() + ":" + films.get(i).getTitre();
        }
        return result;
    }

    public String[] getSallesPourCombo() {
        List<Salle> salles = salleDao.findAll();
        String[] result = new String[salles.size()];
        for (int i = 0; i < salles.size(); i++) {
            result[i] = salles.get(i).getIdSalle() + ":" + salles.get(i).getNom();
        }
        return result;
    }
}