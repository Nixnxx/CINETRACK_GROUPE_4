package main;

import controller.LoginControleur;
import dao.LogDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.PrincipaleView;
import view.Theme;

import java.io.IOException;

public class MainFx extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        afficherLogin();
        stage.show();
    }

    private void afficherLogin() {
        try {
            // login.fxml est chargé ici ; le FXMLLoader crée LoginControleur
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent racine = chargeur.load();
            LoginControleur controleur = chargeur.getController();
            controleur.setSurConnexionReussie(() -> afficherPrincipale());

            stage.setTitle("CinéTrack — Connexion");
            stage.setResizable(false);
            stage.setScene(creerScene(racine, 400, 340));
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger login.fxml", e);
        }
    }

    private void afficherPrincipale() {
        PrincipaleView principale = new PrincipaleView(stage);
        stage.setTitle("CinéTrack — " + LogDAO.getRoleActuel().toUpperCase());
        stage.setResizable(true);
        stage.setScene(creerScene(principale.getRacine(), 1150, 730));
        stage.centerOnScreen();
    }

    private Scene creerScene(Parent racine, double largeur, double hauteur) {
        Scene scene = new Scene(racine, largeur, hauteur);
        Theme.appliquer(scene);
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
