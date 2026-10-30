package viewfx;

import controleur.SeanceControleur;
import controleur.SeanceVue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import modele.Seance;

import java.time.LocalDate;
import java.util.List;

public class SeancesView implements SeanceVue {

    private final Stage fenetre;               // fenêtre propriétaire des boîtes de dialogue
    private final SeanceControleur controleur;
    private final BorderPane racine = new BorderPane();

    private TableView<Seance> tableSeances;
    private final ObservableList<Seance> donnees = FXCollections.observableArrayList();
    private TextField champDate;
    private Label labelNbSeances;
    private Button btnRechercher, btnAjouter, btnSupprimer, btnRafraichir;

    public SeancesView(Stage fenetre) {
        this.fenetre = fenetre;
        this.controleur = new SeanceControleur(this);

        racine.setTop(creerBandeauNord());
        racine.setCenter(creerTable());
        racine.setBottom(creerPanneauBoutons());

        controleur.chargerToutesSeances();
    }

    public BorderPane getRacine() {
        return racine;
    }

    // =========================================================
    //  Construction de l'écran
    // =========================================================
    private HBox creerBandeauNord() {
        labelNbSeances = new Label("Séances");
        labelNbSeances.getStyleClass().add("titre-section");

        Label labelDate = new Label("Date (AAAA-MM-JJ) :");
        labelDate.getStyleClass().add("libelle");

        champDate = new TextField(LocalDate.now().toString());
        champDate.setPrefColumnCount(10);
        champDate.setOnAction(e -> rechercherParDate());   // touche Entrée

        btnRechercher = new Button("Rechercher");
        btnRechercher.getStyleClass().add("bouton-bleu");
        btnRechercher.setOnAction(e -> rechercherParDate());

        HBox filtre = new HBox(8);
        filtre.setAlignment(Pos.CENTER_RIGHT);
        filtre.getChildren().addAll(labelDate, champDate, btnRechercher);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox bandeau = new HBox();
        bandeau.setAlignment(Pos.CENTER);
        bandeau.getStyleClass().add("bandeau");
        bandeau.getChildren().addAll(labelNbSeances, espace, filtre);
        return bandeau;
    }

    private TableView<Seance> creerTable() {
        tableSeances = new TableView<>();
        tableSeances.setItems(donnees);
        tableSeances.setPlaceholder(new Label("Aucune séance"));

        TableColumn<Seance, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdSeance())));
        colId.setPrefWidth(60);

        TableColumn<Seance, String> colFilm = new TableColumn<>("Film");
        colFilm.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTitreFilm()));
        colFilm.setPrefWidth(300);

        TableColumn<Seance, String> colSalle = new TableColumn<>("Salle");
        colSalle.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getNomSalle()));
        colSalle.setPrefWidth(140);

        TableColumn<Seance, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getDateSeance())));
        colDate.setPrefWidth(120);

        TableColumn<Seance, String> colHeure = new TableColumn<>("Heure");
        colHeure.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getHeure())));
        colHeure.setPrefWidth(90);

        TableColumn<Seance, String> colPlaces = new TableColumn<>("Places dispo");
        colPlaces.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getPlacesDispo())));
        colPlaces.setPrefWidth(110);

        tableSeances.getColumns().addAll(colId, colFilm, colSalle, colDate, colHeure, colPlaces);

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

        return tableSeances;
    }

    private HBox creerPanneauBoutons() {
        btnAjouter = new Button("＋ Ajouter une séance");
        btnAjouter.getStyleClass().add("bouton-vert");

        btnSupprimer = new Button("✕ Supprimer");
        btnSupprimer.getStyleClass().add("bouton-rouge");

        btnRafraichir = new Button("↺ Rafraîchir");
        btnRafraichir.getStyleClass().add("bouton-gris");

        btnSupprimer.setDisable(true);

        btnAjouter.setOnAction(e -> actionAjouter());
        btnSupprimer.setOnAction(e -> actionSupprimer());
        btnRafraichir.setOnAction(e -> controleur.chargerToutesSeances());

        Region espace = new Region();
        espace.setPrefWidth(16);

        HBox panneau = new HBox(8);
        panneau.setAlignment(Pos.CENTER_LEFT);
        panneau.getStyleClass().add("barre-boutons");
        panneau.getChildren().addAll(btnAjouter, btnSupprimer, espace, btnRafraichir);
        return panneau;
    }

    // =========================================================
    //  Actions
    // =========================================================
    private void rechercherParDate() {
        controleur.chargerSeancesParDate(champDate.getText().trim());
    }

    private void actionAjouter() {
        DialogueSeanceView d = new DialogueSeanceView(fenetre, controleur);
        if (d.estConfirme()) {
            if (d.getIdFilm() == -1 || d.getIdSalle() == -1) {
                Dialogues.erreur(fenetre, "Choisissez un film et une salle.");
                return;
            }
            controleur.ajouterSeance(d.getIdFilm(), d.getIdSalle(), d.getDate(), d.getHeure(), d.getPlaces(),d.getPrix());
        }
    }

    private void actionSupprimer() {
        Seance seance = tableSeances.getSelectionModel().getSelectedItem();
        if (seance == null) {
            return;
        }
        boolean oui = Dialogues.confirmer(fenetre,
                "Supprimer la séance de \"" + seance.getTitreFilm() + "\" ?");
        if (oui) {
            controleur.supprimerSeance(seance.getIdSeance());
        }
    }

    // Appelée par le bouton « Planning du jour » de la sidebar
    public void chargerPlanningAujourdhui() {
        String aujourdhui = LocalDate.now().toString();
        champDate.setText(aujourdhui);
        controleur.chargerSeancesParDate(aujourdhui);
    }

    // =========================================================
    //  Méthodes appelées par le contrôleur (interface SeanceVue)
    // =========================================================
    @Override
    public void afficherSeances(List<Seance> seances) {
        donnees.setAll(seances);
        labelNbSeances.setText("Séances (" + seances.size() + ")");
    }

    @Override
    public void afficherSucces(String message) {
        Dialogues.info(fenetre, "Succès", message);
    }

    @Override
    public void afficherErreur(String message) {
        Dialogues.erreur(fenetre, message);
    }
}
