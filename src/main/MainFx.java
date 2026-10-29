package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("CinéTrack");
        stage.setScene(new Scene(new Label("JavaFX OK"), 400, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}