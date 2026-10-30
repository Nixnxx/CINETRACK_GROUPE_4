package viewfx;

import controleur.LogControleur;
import controleur.LogVue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import modele.LogEntree;

import java.util.List;

public class LogsView implements LogVue {

    private static final String[] ACTIONS_FILTRE = {
            "Toutes", "INSERT", "UPDATE", "DELETE", "CONNEXION", "RECHERCHE"
    };

    private final Stage fenetre;               // fenêtre propriétaire des boîtes de dialogue
    private final LogControleur controleur;
    private final BorderPane racine = new BorderPane();

    private TableView<LogEntree> tableLogs;
    private final ObservableList<LogEntree> donnees = FXCollections.observableArrayList();
    private Label labelNbLogs;
    private TextField champFiltreTable;
    private ComboBox<String> comboFiltreAction;
    private Button btnRafraichir, btnToutAfficher;

    public LogsView(Stage fenetre) {
        this.fenetre = fenetre;
        this.controleur = new LogControleur(this);

        racine.setTop(creerBandeauNord());
        racine.setCenter(creerTable());
        racine.setBottom(creerPanneauBas());

        // Les filtres sont activés seulement maintenant, quand tous les composants existent
        champFiltreTable.setOnAction(e -> appliquerFiltres());
        comboFiltreAction.setOnAction(e -> appliquerFiltres());

        controleur.chargerLogs(null, null);
    }

    public BorderPane getRacine() {
        return racine;
    }

    // =========================================================
    //  Construction de l'écran
    // =========================================================
    private HBox creerBandeauNord() {
        labelNbLogs = new Label("Logs d'activité");
        labelNbLogs.getStyleClass().add("titre-section");

        Label labelTable = new Label("Table :");
        labelTable.getStyleClass().add("libelle");

        champFiltreTable = new TextField();
        champFiltreTable.setPrefColumnCount(8);

        Label labelAction = new Label("Action :");
        labelAction.getStyleClass().add("libelle");

        comboFiltreAction = new ComboBox<>();
        comboFiltreAction.getItems().addAll(ACTIONS_FILTRE);
        comboFiltreAction.setValue("Toutes");

        btnToutAfficher = new Button("Tout afficher");
        btnToutAfficher.getStyleClass().add("bouton-gris");
        btnToutAfficher.setOnAction(e -> toutAfficher());

        HBox filtres = new HBox(8);
        filtres.setAlignment(Pos.CENTER_RIGHT);
        filtres.getChildren().addAll(labelTable, champFiltreTable, labelAction,
                comboFiltreAction, btnToutAfficher);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox bandeau = new HBox();
        bandeau.setAlignment(Pos.CENTER);
        bandeau.getStyleClass().add("bandeau");
        bandeau.getChildren().addAll(labelNbLogs, espace, filtres);
        return bandeau;
    }

    private TableView<LogEntree> creerTable() {
        tableLogs = new TableView<>();
        tableLogs.setItems(donnees);
        tableLogs.setPlaceholder(new Label("Aucun log"));

        TableColumn<LogEntree, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdLog())));
        colId.setPrefWidth(60);

        TableColumn<LogEntree, String> colTable = new TableColumn<>("Table");
        colTable.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTableCible()));
        colTable.setPrefWidth(90);

        TableColumn<LogEntree, String> colAction = new TableColumn<>("Action");
        colAction.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getAction()));
        colAction.setPrefWidth(100);

        TableColumn<LogEntree, String> colIdCible = new TableColumn<>("ID cible");
        colIdCible.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getIdCible()));
        colIdCible.setPrefWidth(80);

        TableColumn<LogEntree, String> colDetail = new TableColumn<>("Détail");
        colDetail.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDetail()));
        colDetail.setPrefWidth(380);

        TableColumn<LogEntree, String> colPar = new TableColumn<>("Effectué par");
        colPar.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getEffectuePar()));
        colPar.setPrefWidth(120);

        TableColumn<LogEntree, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDateFormatee()));
        colDate.setPrefWidth(160);

        tableLogs.getColumns().addAll(colId, colTable, colAction, colIdCible, colDetail, colPar, colDate);

        // Colore la ligne selon l'action (classes CSS dans theme.css)
        tableLogs.setRowFactory(table -> new TableRow<LogEntree>() {
            @Override
            protected void updateItem(LogEntree log, boolean vide) {
                super.updateItem(log, vide);
                getStyleClass().removeAll("ligne-insert", "ligne-update", "ligne-delete", "ligne-connexion");
                if (!vide && log != null && log.getAction() != null) {
                    switch (log.getAction()) {
                        case "INSERT"    -> getStyleClass().add("ligne-insert");
                        case "UPDATE"    -> getStyleClass().add("ligne-update");
                        case "DELETE"    -> getStyleClass().add("ligne-delete");
                        case "CONNEXION" -> getStyleClass().add("ligne-connexion");
                        default          -> { }
                    }
                }
            }
        });

        return tableLogs;
    }

    private HBox creerPanneauBas() {
        btnRafraichir = new Button("↺ Rafraîchir");
        btnRafraichir.getStyleClass().add("bouton-gris");
        btnRafraichir.setOnAction(e -> appliquerFiltres());

        Region espace = new Region();
        espace.setPrefWidth(20);

        HBox panneau = new HBox(8);
        panneau.setAlignment(Pos.CENTER_LEFT);
        panneau.getStyleClass().add("barre-boutons");
        panneau.getChildren().addAll(btnRafraichir, espace,
                creerBadge("INSERT", "badge-insert"),
                creerBadge("UPDATE", "badge-update"),
                creerBadge("DELETE", "badge-delete"),
                creerBadge("CONNEXION", "badge-connexion"));
        return panneau;
    }

    private Label creerBadge(String texte, String classeCss) {
        Label badge = new Label(texte);
        badge.getStyleClass().addAll("badge-log", classeCss);
        return badge;
    }

    // =========================================================
    //  Actions
    // =========================================================
    private void appliquerFiltres() {
        controleur.chargerLogs(champFiltreTable.getText(), comboFiltreAction.getValue());
    }

    private void toutAfficher() {
        champFiltreTable.setText("");
        comboFiltreAction.setValue("Toutes");   // déclenche déjà un rechargement via setOnAction
        controleur.chargerLogs(null, null);
    }

    // Appelée par PrincipaleView quand on ouvre l'écran, pour voir les logs les plus récents
    public void rafraichir() {
        appliquerFiltres();
    }

    // =========================================================
    //  Méthodes appelées par le contrôleur (interface LogVue)
    // =========================================================
    @Override
    public void afficherLogs(List<LogEntree> logs) {
        donnees.setAll(logs);
        labelNbLogs.setText("Logs d'activité (" + logs.size() + ")");
    }

    @Override
    public void afficherErreur(String message) {
        Dialogues.erreur(fenetre, message);
    }
}
