package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage stage) {
        Label titre = new Label("CinéTrack");
        titre.getStyleClass().add("titre");

        TextField identifiant = new TextField();
        identifiant.setPromptText("Identifiant");

        PasswordField motDePasse = new PasswordField();
        motDePasse.setPromptText("Mot de passe");

        Button connexion = new Button("Connexion");
        connexion.getStyleClass().add("bouton-principal");

        VBox racine = new VBox(12, titre, identifiant, motDePasse, connexion);
        racine.setPadding(new Insets(24));

        Scene scene = new Scene(racine, 360, 300);
        scene.getStylesheets().add(
                getClass().getResource("/css/theme.css").toExternalForm());

        stage.setTitle("CinéTrack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}