package viewfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.Place;
import modele.Seance;
import service.BilletService;

import java.util.ArrayList;
import java.util.List;

public class BilletView {

    private static final String STYLE_LIBRE   = "-fx-background-color: #2a3350; -fx-text-fill: #dce1f0;";
    private static final String STYLE_CHOISIE = "-fx-background-color: #3b82f6; -fx-text-fill: white;";
    private static final String STYLE_VENDUE  = "-fx-background-color: #6b2020; -fx-text-fill: #dce1f0;";

    private final Stage fenetre;
    private final BilletService service = new BilletService();
    private final BorderPane racine = new BorderPane();

    private TableView<Seance> tableSeances;
    private final ObservableList<Seance> donnees = FXCollections.observableArrayList();

    private Label labelSeance;
    private Label labelRecap;
    private GridPane grillePlaces;
    private TextField champNom;
    private Button btnAcheter;

    private Seance seanceChoisie;
    private final List<Place> placesChoisies = new ArrayList<>();

    public BilletView(Stage fenetre) {
        this.fenetre = fenetre;

        racine.setTop(creerBandeau());
        racine.setLeft(creerTable());
        racine.setCenter(creerPanneauAchat());

        chargerSeances();
    }

    public BorderPane getRacine() {
        return racine;
    }
    public void rafraichir() {
        chargerSeances();
    }

    // =========================================================
    //  Construction de l'écran
    // =========================================================
    private HBox creerBandeau() {
        Label titre = new Label("Billetterie");
        titre.getStyleClass().add("titre-section");

        HBox bandeau = new HBox(titre);
        bandeau.setAlignment(Pos.CENTER_LEFT);
        bandeau.getStyleClass().add("bandeau");
        return bandeau;
    }

    private TableView<Seance> creerTable() {
        tableSeances = new TableView<>();
        tableSeances.setItems(donnees);
        tableSeances.setPlaceholder(new Label("Aucune séance à venir"));
        tableSeances.setPrefWidth(560);

        TableColumn<Seance, String> colFilm = new TableColumn<>("Film");
        colFilm.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getTitreFilm()));
        colFilm.setPrefWidth(180);

        TableColumn<Seance, String> colSalle = new TableColumn<>("Salle");
        colSalle.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getNomSalle()));
        colSalle.setPrefWidth(120);

        TableColumn<Seance, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getDateSeance())));
        colDate.setPrefWidth(90);

        TableColumn<Seance, String> colHeure = new TableColumn<>("Heure");
        colHeure.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getHeure())));
        colHeure.setPrefWidth(65);

        TableColumn<Seance, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(l -> new SimpleStringProperty(String.format("%.2f", l.getValue().getPrix())));
        colPrix.setPrefWidth(75);

        tableSeances.getColumns().addAll(colFilm, colSalle, colDate, colHeure, colPrix);

        // Quand on choisit une séance, on affiche sa grille de places
        tableSeances.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancien, nouveau) -> choisirSeance(nouveau));

        return tableSeances;
    }

    private VBox creerPanneauAchat() {
        labelSeance = new Label("Choisissez une séance dans la liste");
        labelSeance.getStyleClass().add("titre-section");

        Label ecran = new Label("───────  ÉCRAN  ───────");
        ecran.getStyleClass().add("libelle");
        ecran.setMaxWidth(Double.MAX_VALUE);
        ecran.setAlignment(Pos.CENTER);

        grillePlaces = new GridPane();
        grillePlaces.setHgap(6);
        grillePlaces.setVgap(6);
        grillePlaces.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(grillePlaces);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Label legende = new Label("Gris : libre   ·   Bleu : choisie   ·   Rouge : déjà vendue");
        legende.getStyleClass().add("libelle");

        Label labelNom = new Label("Nom du client :");
        labelNom.getStyleClass().add("libelle");
        champNom = new TextField();
        champNom.setPrefColumnCount(18);

        btnAcheter = new Button("Acheter");
        btnAcheter.getStyleClass().add("bouton-vert");
        btnAcheter.setDisable(true);
        btnAcheter.setOnAction(e -> acheter());

        HBox ligneAchat = new HBox(10, labelNom, champNom, btnAcheter);
        ligneAchat.setAlignment(Pos.CENTER_LEFT);

        labelRecap = new Label("");
        labelRecap.getStyleClass().add("libelle");

        VBox panneau = new VBox(10, labelSeance, ecran, scroll, legende, labelRecap, ligneAchat);
        panneau.setPadding(new Insets(16));
        return panneau;
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    private void chargerSeances() {
        donnees.setAll(service.seancesAVenir());
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
        afficherPlaces();
    }

    private void afficherPlaces() {
        List<Place> places = service.placeSalle(seanceChoisie.getIdSalle());            // MODIF
        List<Integer> vendues = service.placeVendues(seanceChoisie.getIdSeance());      // MODIF

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
        double total = placesChoisies.size() * seanceChoisie.getPrix();
        labelRecap.setText(placesChoisies.size() + " place(s) : " + noms
                + "   —   Total : " + String.format("%.2f", total));
        btnAcheter.setDisable(false);
    }

    private void acheter() {
        if (seanceChoisie == null || placesChoisies.isEmpty()) {
            return;
        }

        int reussis = 0;
        String erreur = null;
        for (Place p : new ArrayList<>(placesChoisies)) {
            String resultat = service.acheterBillet(champNom.getText(),
                    seanceChoisie.getIdSeance(), p.getId_place(), seanceChoisie.getPrix());
            if (resultat.equals("OK")) {
                reussis++;
            } else {
                erreur = resultat;
                break;
            }
        }

        if (erreur == null) {
            Dialogues.info(fenetre, "Succès", reussis + " billet(s) acheté(s) !");
            champNom.clear();
        } else {
            Dialogues.erreur(fenetre, reussis + " billet(s) acheté(s), puis : " + erreur);
        }

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
}