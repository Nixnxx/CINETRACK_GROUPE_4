package controleur;

import exceptions.SalleIndisponibleException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import modele.Film;
import modele.Salle;
import modele.Seance;
import service.SeanceService;
import viewfx.DialogueSeanceView;
import viewfx.Dialogues;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/*
  SeanceControleur : contrôleur de l'écran seances.fxml.
  JavaFX le crée (fx:controller), remplit les champs @FXML, puis appelle initialize().
 */
public class SeanceControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de seances.fxml)
    @FXML private Label labelNbSeances;
    @FXML private TextField champDate;
    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colId;
    @FXML private TableColumn<Seance, String> colFilm;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colHeure;
    @FXML private TableColumn<Seance, String> colPlaces;
    @FXML private Button btnSupprimer;

    private final SeanceService service = new SeanceService();
    private final ObservableList<Seance> donnees = FXCollections.observableArrayList();

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdSeance())));
        colFilm.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTitreFilm()));
        colSalle.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getNomSalle()));
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getDateSeance())));
        colHeure.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getHeure())));
        colPlaces.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getPlacesDispo())));
        tableSeances.setItems(donnees);

        // Colore la ligne selon les places restantes (classes CSS dans theme.css)
        tableSeances.setRowFactory(table -> new TableRow<Seance>() {
            @Override
            protected void updateItem(Seance seance, boolean vide) {
                super.updateItem(seance, vide);
                getStyleClass().removeAll("ligne-places-zero", "ligne-places-faibles");
                if (!vide && seance != null) {
                    if (seance.getPlacesDispo() == 0) {
                        getStyleClass().add("ligne-places-zero");
                    } else if (seance.getPlacesDispo() < 20) {
                        getStyleClass().add("ligne-places-faibles");
                    }
                }
            }
        });

        // Le bouton Supprimer n'est actif que si une ligne est sélectionnée
        tableSeances.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) ->
                btnSupprimer.setDisable(nouveau == null));

        champDate.setText(LocalDate.now().toString());

        chargerToutesSeances();
    }

    // =========================================================
    //  Actions des boutons (appelées par seances.fxml)
    // =========================================================
    @FXML
    private void onRechercher() {
        chargerSeancesParDate(champDate.getText().trim());
    }

    @FXML
    private void onAjouter() {
        DialogueSeanceView d = new DialogueSeanceView((Stage) fenetre(), this);
        if (d.estConfirme()) {
            if (d.getIdFilm() == -1 || d.getIdSalle() == -1) {
                afficherErreur("Choisissez un film et une salle.");
                return;
            }
            ajouterSeance(d.getIdFilm(), d.getIdSalle(), d.getDate(), d.getHeure(), d.getPlaces(), d.getPrix());
        }
    }

    @FXML
    private void onSupprimer() {
        Seance seance = tableSeances.getSelectionModel().getSelectedItem();
        if (seance == null) {
            return;
        }
        boolean oui = Dialogues.confirmer(fenetre(),
                "Supprimer la séance de \"" + seance.getTitreFilm() + "\" ?");
        if (oui) {
            supprimerSeance(seance.getIdSeance());
        }
    }

    @FXML
    private void onRafraichir() {
        chargerToutesSeances();
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    public void chargerToutesSeances() {
        afficherSeances(service.getToutesSeances());
    }

    public void chargerSeancesParDate(String date) {
        try {
            afficherSeances(service.getSeancesParDate(date));
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
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
                afficherSucces("Séance ajoutée avec succès !");
                chargerToutesSeances();
            } else {
                afficherErreur("Erreur lors de l'ajout de la séance.");
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            afficherErreur("Données invalides (date YYYY-MM-DD, heure HH:MM:SS, places et prix numériques)");
        } catch (SalleIndisponibleException | IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    public void supprimerSeance(int idSeance) {
        try {
            if (service.supprimerSeance(idSeance)) {
                afficherSucces("Séance supprimée !");
                chargerToutesSeances();
            } else {
                afficherErreur("Erreur lors de la suppression");
            }
        } catch (IllegalStateException e) {
            afficherErreur(e.getMessage());
        }
    }

    // Utilisées par DialogueSeanceView pour remplir les listes déroulantes
    public List<Film> getTousFilms() {
        return service.getTousFilms();
    }

    public List<Salle> getToutesSalles() {
        return service.getToutesSalles();
    }

    // Appelée par le bouton « Planning du jour » de la sidebar
    public void chargerPlanningAujourdhui() {
        String aujourdhui = LocalDate.now().toString();
        champDate.setText(aujourdhui);
        chargerSeancesParDate(aujourdhui);
    }

    // =========================================================
    //  Affichage
    // =========================================================
    private void afficherSeances(List<Seance> seances) {
        donnees.setAll(seances);
        labelNbSeances.setText("Séances (" + seances.size() + ")");
    }

    private void afficherSucces(String message) {
        Dialogues.info(fenetre(), "Succès", message);
    }

    private void afficherErreur(String message) {
        Dialogues.erreur(fenetre(), message);
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (tableSeances.getScene() == null) {
            return null;
        }
        return tableSeances.getScene().getWindow();
    }
}
