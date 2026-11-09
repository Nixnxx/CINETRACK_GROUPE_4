package controleur;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import modele.Utilisateur;
import service.UtilisateurService;
import viewfx.Dialogues;

import java.sql.SQLException;
import java.util.List;

/*
  UtilisateurControleur : contrôleur de l'écran utilisateurs.fxml
  (gestion des comptes gestionnaires, admin uniquement).
  La connexion a maintenant son propre contrôleur : LoginControleur.
 */
public class UtilisateurControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de utilisateurs.fxml)
    @FXML private Label labelTitre;
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colId;
    @FXML private TableColumn<Utilisateur, String> colLogin;
    @FXML private TableColumn<Utilisateur, String> colDate;
    @FXML private TextField champLogin;
    @FXML private PasswordField champMotDePasse;
    @FXML private PasswordField champConfirmation;
    @FXML private Button btnSupprimer;

    private final UtilisateurService service = new UtilisateurService();
    private final ObservableList<Utilisateur> donnees = FXCollections.observableArrayList();

    // Gardées ici pour ne pas modifier les classes qui les appellent déjà
    public static Utilisateur getUtilisateurCourant() {
        return UtilisateurService.getUtilisateurCourant();
    }

    public static boolean estAdminConnecte() {
        return UtilisateurService.estAdminConnecte();
    }

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdUtilisateur())));
        colLogin.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getLogin()));
        colDate.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDateCreationFormatee()));
        tableUtilisateurs.setItems(donnees);

        // Le bouton Supprimer n'est actif que si une ligne est sélectionnée
        tableUtilisateurs.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) ->
                btnSupprimer.setDisable(nouveau == null));

        chargerGestionnaires();
    }

    // =========================================================
    //  Actions des boutons (appelées par utilisateurs.fxml)
    // =========================================================
    @FXML
    private void onAjouter() {
        ajouterGestionnaire(champLogin.getText(), champMotDePasse.getText(), champConfirmation.getText());
    }

    @FXML
    private void onSupprimer() {
        Utilisateur utilisateur = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (utilisateur == null) {
            return;
        }
        supprimerGestionnaire(utilisateur.getIdUtilisateur(), utilisateur.getLogin());
    }

    @FXML
    private void onRafraichir() {
        chargerGestionnaires();
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    public void chargerGestionnaires() {
        try {
            afficherGestionnaires(service.getGestionnaires());
        } catch (IllegalStateException e) {
            afficherErreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void ajouterGestionnaire(String login, String motDePasse, String confirmation) {
        try {
            service.ajouterGestionnaire(login, motDePasse, confirmation);

            afficherSucces("Gestionnaire \"" + login.trim() + "\" créé avec succès !");
            viderFormulaire();
            chargerGestionnaires();
        } catch (IllegalArgumentException | IllegalStateException e) {
            afficherErreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    public void supprimerGestionnaire(int idUtilisateur, String login) {
        try {
            service.verifierAdmin();   // avant de demander la confirmation

            if (!Dialogues.confirmer(fenetre(), "Supprimer le gestionnaire \"" + login + "\" ?")) {
                return;
            }
            if (service.supprimerGestionnaire(idUtilisateur, login)) {
                afficherSucces("Gestionnaire supprimé !");
                chargerGestionnaires();
            } else {
                afficherErreur("Suppression impossible : compte introuvable ou non supprimable.");
            }
        } catch (IllegalStateException e) {
            afficherErreur(e.getMessage());
        } catch (SQLException e) {
            erreurBase(e);
        }
    }

    // =========================================================
    //  Affichage
    // =========================================================
    private void afficherGestionnaires(List<Utilisateur> utilisateurs) {
        donnees.setAll(utilisateurs);
        labelTitre.setText("Gestionnaires (" + utilisateurs.size() + ")");
        btnSupprimer.setDisable(true);
    }

    private void viderFormulaire() {
        champLogin.setText("");
        champMotDePasse.setText("");
        champConfirmation.setText("");
        champLogin.requestFocus();
    }

    private void afficherSucces(String message) {
        Dialogues.info(fenetre(), "Succès", message);
    }

    private void afficherErreur(String message) {
        Dialogues.erreur(fenetre(), message);
    }

    private void erreurBase(SQLException e) {
        e.printStackTrace();
        afficherErreur("Erreur base de données : " + e.getMessage());
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (tableUtilisateurs.getScene() == null) {
            return null;
        }
        return tableUtilisateurs.getScene().getWindow();
    }
}
