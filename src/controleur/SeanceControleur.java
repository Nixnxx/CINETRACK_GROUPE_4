package controleur;

import exceptions.SalleIndisponibleException;
import modele.Film;
import modele.Salle;
import service.SeanceService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SeanceControleur {
    private final SeanceVue vue;
    private final SeanceService service;

    public SeanceControleur(SeanceVue vue) {
        this.vue = vue;
        this.service = new SeanceService();
    }

    public void chargerToutesSeances() {
        vue.afficherSeances(service.getToutesSeances());
    }

    public void chargerSeancesParDate(String date) {
        try {
            vue.afficherSeances(service.getSeancesParDate(date));
        } catch (IllegalArgumentException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void ajouterSeance(int idFilm, int idSalle, String dateStr, String heureStr,
                              String placesStr, String prixStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heure = LocalTime.parse(heureStr);
            int places = Integer.parseInt(placesStr);
            double prix = Double.parseDouble(prixStr);

            if (service.ajouterSeance(idFilm, idSalle, date, heure, places, prix)) {
                vue.afficherSucces("Séance ajoutée avec succès !");
                chargerToutesSeances();
            } else {
                vue.afficherErreur("Erreur lors de l'ajout de la séance.");
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            vue.afficherErreur("Données invalides (date YYYY-MM-DD, heure HH:MM:SS, places et prix numériques)");
        } catch (SalleIndisponibleException | IllegalArgumentException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    public void supprimerSeance(int idSeance) {
        try {
            if (service.supprimerSeance(idSeance)) {
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
        return service.getTousFilms();
    }

    public List<Salle> getToutesSalles() {
        return service.getToutesSalles();
    }

    public String[] getFilmsPourCombo() {
        List<Film> films = service.getTousFilms();
        String[] result = new String[films.size()];
        for (int i = 0; i < films.size(); i++) {
            result[i] = films.get(i).getIdFilm() + ":" + films.get(i).getTitre();
        }
        return result;
    }

    public String[] getSallesPourCombo() {
        List<Salle> salles = service.getToutesSalles();
        String[] result = new String[salles.size()];
        for (int i = 0; i < salles.size(); i++) {
            result[i] = salles.get(i).getIdSalle() + ":" + salles.get(i).getNom();
        }
        return result;
    }
}