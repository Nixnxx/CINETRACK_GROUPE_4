package viewfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

public class Dialogues {

    public static void info(Window parent, String titre, String message) {
        Alert alerte = creerAlerte(Alert.AlertType.INFORMATION, parent, titre, message);
        alerte.showAndWait();
    }

    public static void erreur(Window parent, String message) {
        Alert alerte = creerAlerte(Alert.AlertType.ERROR, parent, "Erreur", message);
        alerte.showAndWait();
    }

    public static boolean confirmer(Window parent, String message) {
        Alert alerte = creerAlerte(Alert.AlertType.CONFIRMATION, parent, "Confirmation", message);
        alerte.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> reponse = alerte.showAndWait();
        return reponse.isPresent() && reponse.get() == ButtonType.YES;
    }

    private static Alert creerAlerte(Alert.AlertType type, Window parent, String titre, String message) {
        Alert alerte = new Alert(type, message);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        if (parent != null) {
            alerte.initOwner(parent);
        }
        alerte.getDialogPane().getStylesheets().add(Theme.CSS);
        return alerte;
    }
}