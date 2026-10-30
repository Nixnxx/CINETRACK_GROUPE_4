package main;

import dao.LogDAO;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import viewfx.LoginView;

public class MainFx extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        afficherLogin();
        stage.show();
    }

    private void afficherLogin() {
        LoginView login = new LoginView(this::afficherAccueilProvisoire);
        stage.setTitle("CinéTrack — Connexion");
        stage.setResizable(false);
        stage.setScene(creerScene(login.getRacine(), 400, 340));
        stage.centerOnScreen();
    }

    private void afficherAccueilProvisoire() {
        String role = LogDAO.getRoleActuel();

        Label titre = new Label("Bienvenue dans CinéTrack");
        titre.getStyleClass().add("titre");
        Label sousTitre = new Label("Connecté en tant que : " + role);
        sousTitre.getStyleClass().add("sous-titre");

        VBox contenu = new VBox(10, titre, sousTitre);
        contenu.setAlignment(Pos.CENTER);

        stage.setTitle("CinéTrack — " + role.toUpperCase());
        stage.setResizable(true);
        stage.setScene(creerScene(contenu, 1150, 730));
        stage.centerOnScreen();
    }

    private Scene creerScene(Parent racine, double largeur, double hauteur) {
        Scene scene = new Scene(racine, largeur, hauteur);
        scene.getStylesheets().add(
                getClass().getResource("/css/theme.css").toExternalForm());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}