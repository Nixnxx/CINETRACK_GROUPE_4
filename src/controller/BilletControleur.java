package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import model.Place;
import model.Seance;
import service.BilletService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
  BilletControleur : contrôleur de l'écran billetterie.fxml.
  JavaFX le crée (fx:controller), remplit les champs @FXML, puis appelle initialize().
 */
public class BilletControleur {

    private static final String STYLE_LIBRE   = "-fx-background-color: #2a3350; -fx-text-fill: #dce1f0;";
    private static final String STYLE_CHOISIE = "-fx-background-color: #3b82f6; -fx-text-fill: white;";
    private static final String STYLE_VENDUE  = "-fx-background-color: #6b2020; -fx-text-fill: #dce1f0;";

    // Éléments de l'écran (les noms correspondent aux fx:id de billetterie.fxml)
    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colFilm;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colHeure;
    @FXML private TableColumn<Seance, String> colPrix;
    @FXML private Label labelSeance;
    @FXML private Label labelRecap;
    @FXML private GridPane grillePlaces;
    @FXML private TextField champNom;
    @FXML private DatePicker champDateNaissance;
    @FXML private Button btnAcheter;

    private final BilletService service = new BilletService();
    private final ObservableList<Seance> donnees = FXCollections.observableArrayList();

    private Seance seanceChoisie;
    private final List<Place> placesChoisies = new ArrayList<>();

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colFilm.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTitreFilm()));
        colSalle.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getNomSalle()));
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getDateSeance())));
        colHeure.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getHeure())));
        colPrix.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.format("%.2f", ligne.getValue().getPrix())));
        tableSeances.setItems(donnees);

        // Quand on choisit la date de naissance, le récapitulatif affiche le prix avec la remise
        champDateNaissance.valueProperty().addListener((obs, ancien, nouveau) -> majRecap());

        // Quand on choisit une séance, on affiche sa grille de places
        tableSeances.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancien, nouveau) -> choisirSeance(nouveau));

        chargerSeances();
    }

    // =========================================================
    //  Action du bouton (appelée par billetterie.fxml)
    // =========================================================
    @FXML
    private void onAcheter() {
        if (seanceChoisie == null || placesChoisies.isEmpty()) {
            return;
        }

        acheterBillets(champNom.getText(), champDateNaissance.getValue(), seanceChoisie.getIdSeance(),
                seanceChoisie.getPrix(), new ArrayList<>(placesChoisies));

        // Recharge la liste puis réaffiche la même séance, avec les places à jour
        int idSeance = seanceChoisie.getIdSeance();
        chargerSeances();
        for (Seance s : donnees) {
            if (s.getIdSeance() == idSeance) {
                tableSeances.getSelectionModel().select(s);
                break;
            }
        }
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    public void chargerSeances() {
        donnees.setAll(service.seancesAVenir());
    }

    // Appelée par PrincipaleControleur à chaque ouverture de l'écran (séances à jour)
    public void rafraichir() {
        chargerSeances();
    }

    private void choisirSeance(Seance seance) {
        seanceChoisie = seance;
        placesChoisies.clear();
        grillePlaces.getChildren().clear();

        if (seance == null) {
            labelSeance.setText("Choisissez une séance dans la liste");
            majRecap();
            return;
        }

        labelSeance.setText(seance.getTitreFilm() + " — " + seance.getNomSalle()
                + " — " + seance.getDateSeance() + " " + seance.getHeure());
        afficherPlaces(service.placeSalle(seance.getIdSalle()),
                service.placeVendues(seance.getIdSeance()));
    }

    // Achète une place après l'autre, et s'arrête à la première erreur
    // prix = prix de base de la séance : la remise est calculée par le service
    private void acheterBillets(String nomClient, LocalDate dateNaissance, int idSeance, double prix, List<Place> places) {
        int reussis = 0;
        String erreur = null;

        for (Place p : places) {
            String resultat = service.acheterBillet(nomClient, dateNaissance, idSeance, p.getId_place(), prix);
            if (resultat.equals("OK")) {
                reussis++;
            } else {
                erreur = resultat;
                break;
            }
        }

        if (erreur == null) {
            DialogueMessageControleur.info(fenetre(), "Succès", reussis + " billet(s) acheté(s) !");
            champNom.clear();
            champDateNaissance.setValue(null);
        } else {
            DialogueMessageControleur.erreur(fenetre(), reussis + " billet(s) acheté(s), puis : " + erreur);
        }
    }

    // =========================================================
    //  Affichage
    // =========================================================
    private void afficherPlaces(List<Place> places, List<Integer> vendues) {
        for (Place p : places) {
            ToggleButton bouton = new ToggleButton(p.getRangee() + p.getNumero());
            bouton.setPrefSize(52, 30);

            if (vendues.contains(p.getId_place())) {
                bouton.setDisable(true);
                bouton.setStyle(STYLE_VENDUE);
            } else {
                bouton.setStyle(STYLE_LIBRE);
                bouton.selectedProperty().addListener((obs, avant, choisie) -> {
                    if (choisie) {
                        placesChoisies.add(p);
                    } else {
                        placesChoisies.remove(p);
                    }
                    bouton.setStyle(choisie ? STYLE_CHOISIE : STYLE_LIBRE);
                    majRecap();
                });
            }

            int ligne = p.getRangee().charAt(0) - 'A';   // A -> 0, B -> 1...
            int colonne = p.getNumero() - 1;             // 1 -> 0, 2 -> 1...
            grillePlaces.add(bouton, colonne, ligne);
        }
        majRecap();
    }

    private void majRecap() {
        if (seanceChoisie == null || placesChoisies.isEmpty()) {
            labelRecap.setText("");
            btnAcheter.setDisable(true);
            return;
        }
        StringBuilder noms = new StringBuilder();
        for (Place p : placesChoisies) {
            if (noms.length() > 0) {
                noms.append(", ");
            }
            noms.append(p.getRangee()).append(p.getNumero());
        }
        // Prix d'une place : avec la remise si la date de naissance est saisie et valide
        double prixPlace = seanceChoisie.getPrix();
        String infoRemise = "";
        LocalDate naissance = champDateNaissance.getValue();
        if (naissance != null && !naissance.isAfter(LocalDate.now())) {
            prixPlace = service.prixFinal(seanceChoisie.getPrix(), naissance);
            if (service.aDroitRemise(naissance)) {
                infoRemise = "  (remise de 20 % incluse)";
            }
        }
        double total = placesChoisies.size() * prixPlace;
        labelRecap.setText(placesChoisies.size() + " place(s) : " + noms
                + "   —   Total : " + String.format("%.2f", total) + infoRemise);
        btnAcheter.setDisable(false);
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (tableSeances.getScene() == null) {
            return null;
        }
        return tableSeances.getScene().getWindow();
    }
}
