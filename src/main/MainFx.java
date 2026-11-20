package main;

import controller.LoginControleur;
import dao.LogDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        try {
            // principale.fxml est chargé ici ; le FXMLLoader crée PrincipaleControleur
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("/view/principale.fxml"));
            Parent racine = chargeur.load();

            stage.setTitle("CinéTrack — " + LogDAO.getRoleActuel().toUpperCase());
            stage.setResizable(true);
            stage.setScene(creerScene(racine, 1150, 730));
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger principale.fxml", e);
        }
    }

    private Scene creerScene(Parent racine, double largeur, double hauteur) {
        return new Scene(racine, largeur, hauteur);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
