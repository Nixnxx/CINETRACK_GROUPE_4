package controleur;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import modele.LogEntree;
import service.LogService;
import viewfx.Dialogues;

import java.sql.SQLException;
import java.util.List;

/*
  LogControleur : contrôleur de l'écran logs.fxml.
  JavaFX le crée (fx:controller), remplit les champs @FXML, puis appelle initialize().
 */
public class LogControleur {

    private static final String[] ACTIONS_FILTRE = {
            "Toutes", "INSERT", "UPDATE", "DELETE", "CONNEXION", "RECHERCHE"
    };

    // Éléments de l'écran (les noms correspondent aux fx:id de logs.fxml)
    @FXML private Label labelNbLogs;
    @FXML private TextField champFiltreTable;
    @FXML private ComboBox<String> comboFiltreAction;
    @FXML private TableView<LogEntree> tableLogs;
    @FXML private TableColumn<LogEntree, String> colId;
    @FXML private TableColumn<LogEntree, String> colTable;
    @FXML private TableColumn<LogEntree, String> colAction;
    @FXML private TableColumn<LogEntree, String> colIdCible;
    @FXML private TableColumn<LogEntree, String> colDetail;
    @FXML private TableColumn<LogEntree, String> colPar;
    @FXML private TableColumn<LogEntree, String> colDate;

    private final LogService service = new LogService();
    private final ObservableList<LogEntree> donnees = FXCollections.observableArrayList();

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdLog())));
        colTable.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTableCible()));
        colAction.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getAction()));
        colIdCible.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getIdCible()));
        colDetail.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDetail()));
        colPar.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getEffectuePar()));
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDateFormatee()));
        tableLogs.setItems(donnees);

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

        comboFiltreAction.getItems().addAll(ACTIONS_FILTRE);
        comboFiltreAction.setValue("Toutes");
        // Le filtre n'est activé qu'une fois la valeur de départ posée
        comboFiltreAction.setOnAction(e -> onFiltrer());

        chargerLogs(null, null);
    }

    // =========================================================
    //  Actions (appelées par logs.fxml)
    // =========================================================
    @FXML
    private void onFiltrer() {
        chargerLogs(champFiltreTable.getText(), comboFiltreAction.getValue());
    }

    @FXML
    private void onToutAfficher() {
        champFiltreTable.setText("");
        comboFiltreAction.setValue("Toutes");   // déclenche déjà un rechargement via setOnAction
        chargerLogs(null, null);
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    // Les deux filtres peuvent être vides : on affiche alors tous les logs
    public void chargerLogs(String filtreTable, String filtreAction) {
        String action = filtreAction;
        if ("Toutes".equals(filtreAction)) {
            action = null;   // "Toutes" est un libellé de la liste déroulante
        }

        try {
            List<LogEntree> logs = service.getLogs(filtreTable, action);
            donnees.setAll(logs);
            labelNbLogs.setText("Logs d'activité (" + logs.size() + ")");
        } catch (SQLException e) {
            Dialogues.erreur(fenetre(), "Erreur chargement des logs :\n" + e.getMessage());
        }
    }

    // Appelée par PrincipaleView quand on ouvre l'écran, pour voir les logs les plus récents
    public void rafraichir() {
        onFiltrer();
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (tableLogs.getScene() == null) {
            return null;
        }
        return tableLogs.getScene().getWindow();
    }
}
