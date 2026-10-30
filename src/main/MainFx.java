package main;

import dao.LogDAO;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import viewfx.LoginView;
import viewfx.PrincipaleView;
import viewfx.Theme;

public class MainFx extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        afficherLogin();
        stage.show();
    }

    private void afficherLogin() {
        LoginView login = new LoginView(() -> afficherPrincipale());
        stage.setTitle("CinéTrack — Connexion");
        stage.setResizable(false);
        stage.setScene(creerScene(login.getRacine(), 400, 340));
        stage.centerOnScreen();
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