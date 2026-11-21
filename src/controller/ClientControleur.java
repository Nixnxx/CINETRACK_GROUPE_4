package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.ClientResume;
import service.ClientService;

import java.time.format.DateTimeFormatter;
import java.util.List;

/*
  ClientControleur : contrôleur de l'écran clients.fxml (réservé à l'admin).
  JavaFX le crée (fx:controller), remplit les champs @FXML, puis appelle initialize().
  L'écran est en lecture seule : il affiche chaque client, son nombre de billets
  valides et le total qu'il a payé.
 */
public class ClientControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de clients.fxml)
    @FXML private Label labelNbClients;
    @FXML private TableView<ClientResume> tableClients;
    @FXML private TableColumn<ClientResume, String> colNom;
    @FXML private TableColumn<ClientResume, String> colNaissance;
    @FXML private TableColumn<ClientResume, String> colNbBillets;
    @FXML private TableColumn<ClientResume, String> colTotal;

    private final ClientService service = new ClientService();
    private final ObservableList<ClientResume> donnees = FXCollections.observableArrayList();
    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colNom.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getNom()));
        colNaissance.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDateNaissance().format(formatDate)));
        colNbBillets.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getNbBillets())));
        colTotal.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.format("%.2f", ligne.getValue().getTotalPaye())));
        tableClients.setItems(donnees);

        chargerClients();
    }

    // =========================================================
    //  Action du bouton (appelée par clients.fxml)
    // =========================================================
    @FXML
    private void onRafraichir() {
        chargerClients();
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    public void chargerClients() {
        List<ClientResume> clients = service.getResumeClients();
        donnees.setAll(clients);
        labelNbClients.setText("Clients (" + clients.size() + ")");
    }

    // Appelée par PrincipaleControleur à chaque ouverture de l'écran (achats récents à jour)
    public void rafraichir() {
        chargerClients();
    }
}
