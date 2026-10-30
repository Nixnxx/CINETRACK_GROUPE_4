package viewfx;

import controleur.UtilisateurControleur;
import controleur.UtilisateurVue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.Utilisateur;

import java.util.List;

// Écran réservé à l'admin : créer / supprimer les comptes gestionnaires
public class UtilisateursView implements UtilisateurVue {

    private final Stage fenetre;               // fenêtre propriétaire des boîtes de dialogue
    private final UtilisateurControleur controleur;
    private final BorderPane racine = new BorderPane();

    private TableView<Utilisateur> tableUtilisateurs;
    private final ObservableList<Utilisateur> donnees = FXCollections.observableArrayList();
    private Label labelTitre;
    private TextField champLogin;
    private PasswordField champMotDePasse;
    private PasswordField champConfirmation;
    private Button btnAjouter, btnSupprimer, btnRafraichir;

    public UtilisateursView(Stage fenetre) {
        this.fenetre = fenetre;
        this.controleur = new UtilisateurControleur(this);

        racine.setTop(creerBandeauNord());
        racine.setCenter(creerTable());
        racine.setBottom(creerPanneauBas());

        controleur.chargerGestionnaires();
    }

    public BorderPane getRacine() {
        return racine;
    }

    // =========================================================
    //  Construction de l'écran
    // =========================================================
    private HBox creerBandeauNord() {
        labelTitre = new Label("Gestionnaires");
        labelTitre.getStyleClass().add("titre-section");

        Label info = new Label("⚙  Administration");
        info.getStyleClass().add("admin-info");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox bandeau = new HBox();
        bandeau.setAlignment(Pos.CENTER);
        bandeau.getStyleClass().add("bandeau");
        bandeau.getChildren().addAll(labelTitre, espace, info);
        return bandeau;
    }

    private TableView<Utilisateur> creerTable() {
        tableUtilisateurs = new TableView<>();
        tableUtilisateurs.setItems(donnees);
        tableUtilisateurs.setPlaceholder(new Label("Aucun gestionnaire"));

        TableColumn<Utilisateur, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdUtilisateur())));
        colId.setPrefWidth(60);

        TableColumn<Utilisateur, String> colLogin = new TableColumn<>("Identifiant");
        colLogin.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getLogin()));
        colLogin.setPrefWidth(300);

        TableColumn<Utilisateur, String> colDate = new TableColumn<>("Créé le");
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getDateCreationFormatee())));
        colDate.setPrefWidth(200);

        tableUtilisateurs.getColumns().addAll(colId, colLogin, colDate);

        // Le bouton Supprimer n'est actif que si une ligne est sélectionnée
        tableUtilisateurs.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) ->
                btnSupprimer.setDisable(nouveau == null));

        return tableUtilisateurs;
    }

    // Bas de l'écran : formulaire de création, puis barre de boutons
    private VBox creerPanneauBas() {
        VBox bas = new VBox();
        bas.getChildren().addAll(creerFormulaire(), creerPanneauBoutons());
        return bas;
    }

    private HBox creerFormulaire() {
        champLogin = new TextField();
        champLogin.setPrefColumnCount(10);

        champMotDePasse = new PasswordField();
        champMotDePasse.setPrefColumnCount(10);

        champConfirmation = new PasswordField();
        champConfirmation.setPrefColumnCount(10);
        champConfirmation.setOnAction(e -> actionAjouter());   // touche Entrée

        btnAjouter = new Button("＋ Ajouter");
        btnAjouter.getStyleClass().add("bouton-vert");
        btnAjouter.setOnAction(e -> actionAjouter());

        HBox formulaire = new HBox(8);
        formulaire.setAlignment(Pos.CENTER_LEFT);
        formulaire.getStyleClass().add("formulaire-ligne");
        formulaire.getChildren().addAll(
                creerLabel("Identifiant :"), champLogin,
                creerLabel("Mot de passe :"), champMotDePasse,
                creerLabel("Confirmation :"), champConfirmation,
                btnAjouter);
        return formulaire;
    }

    private HBox creerPanneauBoutons() {
        btnSupprimer = new Button("✕ Supprimer");
        btnSupprimer.getStyleClass().add("bouton-rouge");
        btnSupprimer.setDisable(true);
        btnSupprimer.setOnAction(e -> actionSupprimer());

        btnRafraichir = new Button("↺ Rafraîchir");
        btnRafraichir.getStyleClass().add("bouton-gris");
        btnRafraichir.setOnAction(e -> controleur.chargerGestionnaires());

        Region espace = new Region();
        espace.setPrefWidth(16);

        HBox panneau = new HBox(8);
        panneau.setAlignment(Pos.CENTER_LEFT);
        panneau.getStyleClass().add("barre-boutons");
        panneau.getChildren().addAll(btnSupprimer, espace, btnRafraichir);
        return panneau;
    }

    private Label creerLabel(String texte) {
        Label label = new Label(texte);
        label.getStyleClass().add("libelle");
        return label;
    }

    // =========================================================
    //  Actions
    // =========================================================
    private void actionAjouter() {
        controleur.ajouterGestionnaire(
                champLogin.getText(),
                champMotDePasse.getText(),
                champConfirmation.getText());
    }

    private void actionSupprimer() {
        Utilisateur utilisateur = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (utilisateur == null) {
            return;
        }
        controleur.supprimerGestionnaire(utilisateur.getIdUtilisateur(), utilisateur.getLogin());
    }

    // =========================================================
    //  Méthodes appelées par le contrôleur (interface UtilisateurVue)
    // =========================================================
    @Override
    public void afficherGestionnaires(List<Utilisateur> utilisateurs) {
        donnees.setAll(utilisateurs);
        labelTitre.setText("Gestionnaires (" + utilisateurs.size() + ")");
        btnSupprimer.setDisable(true);
    }

    @Override
    public void viderFormulaire() {
        champLogin.setText("");
        champMotDePasse.setText("");
        champConfirmation.setText("");
        champLogin.requestFocus();
    }

    @Override
    public void afficherSucces(String message) {
        Dialogues.info(fenetre, "Succès", message);
    }

    @Override
    public void afficherErreur(String message) {
        Dialogues.erreur(fenetre, message);
    }

    @Override
    public boolean demanderConfirmation(String message) {
        return Dialogues.confirmer(fenetre, message);
    }
}
