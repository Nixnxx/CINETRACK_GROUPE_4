package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/*
  DialogueMessageControleur : contrôleur de la fenêtre dialogue-message.fxml.
  Elle sert à afficher une information, une erreur ou une demande de confirmation.
  Les trois méthodes static s'utilisent partout comme avant :
      DialogueMessageControleur.info(fenetre, "Succès", "Film ajouté !");
      DialogueMessageControleur.erreur(fenetre, "Note invalide");
      if (DialogueMessageControleur.confirmer(fenetre, "Supprimer ?")) { ... }
 */
public class DialogueMessageControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de dialogue-message.fxml)
    @FXML private Label labelMessage;
    @FXML private Button btnNon;
    @FXML private Button btnOui;

    private Stage fenetre;
    private boolean reponseOui = false;

    public static void info(Window parent, String titre, String message) {
        afficher(parent, titre, message, false);
    }

    public static void erreur(Window parent, String message) {
        afficher(parent, "Erreur", message, false);
    }

    // Retourne true si l'utilisateur clique sur « Oui »
    public static boolean confirmer(Window parent, String message) {
        return afficher(parent, "Confirmation", message, true);
    }

    // Charge le FXML, affiche la fenêtre et attend sa fermeture
    private static boolean afficher(Window parent, String titre, String message, boolean avecChoix) {
        try {
            FXMLLoader chargeur = new FXMLLoader(
                    DialogueMessageControleur.class.getResource("/view/dialogue-message.fxml"));
            Parent racine = chargeur.load();
            DialogueMessageControleur controleur = chargeur.getController();
            return controleur.ouvrir(parent, racine, titre, message, avecChoix);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger dialogue-message.fxml", e);
        }
    }

    private boolean ouvrir(Window parent, Parent racine, String titre, String message, boolean avecChoix) {
        labelMessage.setText(message);
        if (avecChoix) {
            btnNon.setText("Non");
            btnOui.setText("Oui");
        } else {
            btnNon.setVisible(false);   // un seul bouton « OK » pour une information ou une erreur
            btnNon.setManaged(false);
        }

        fenetre = new Stage();
        if (parent != null) {
            fenetre.initOwner(parent);
            fenetre.initModality(Modality.WINDOW_MODAL);
        } else {
            fenetre.initModality(Modality.APPLICATION_MODAL);
        }
        fenetre.setResizable(false);
        fenetre.setTitle(titre);
        fenetre.setScene(new Scene(racine));
        fenetre.showAndWait();   // attend la fermeture
        return reponseOui;
    }

    // =========================================================
    //  Boutons (appelés par dialogue-message.fxml)
    // =========================================================
    @FXML
    private void onOui() {
        reponseOui = true;
        fenetre.close();
    }

    @FXML
    private void onNon() {
        fenetre.close();
    }
}
