package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Film;

import java.io.IOException;

/*
  DialogueFilmControleur : contrôleur du formulaire dialogue-film.fxml
  (ajout ou modification d'un film, dans une fenêtre modale).
 */
public class DialogueFilmControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de dialogue-film.fxml)
    @FXML private TextField champTitre;
    @FXML private ComboBox<String> comboGenre;
    @FXML private TextField champDuree;
    @FXML private TextField champNote;
    @FXML private TextArea champSynopsis;

    private Stage fenetre;
    private boolean confirme = false;

    // Charge le FXML, affiche la fenêtre et attend sa fermeture.
    // filmExistant == null : ajout ; sinon : modification
    public static DialogueFilmControleur ouvrir(Window parent, Film filmExistant) {
        try {
            FXMLLoader chargeur = new FXMLLoader(
                    DialogueFilmControleur.class.getResource("/view/dialogue-film.fxml"));
            Parent racine = chargeur.load();
            DialogueFilmControleur controleur = chargeur.getController();
            controleur.afficher(parent, racine, filmExistant);
            return controleur;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger dialogue-film.fxml", e);
        }
    }

    private void afficher(Window parent, Parent racine, Film filmExistant) {
        fenetre = new Stage();
        fenetre.initOwner(parent);
        fenetre.initModality(Modality.WINDOW_MODAL);
        fenetre.setResizable(false);
        if (filmExistant == null) {
            fenetre.setTitle("Ajouter un film");
        } else {
            fenetre.setTitle("Modifier le film");
            preRemplir(filmExistant);
        }

        Scene scene = new Scene(racine);
        fenetre.setScene(scene);
        fenetre.showAndWait();   // attend la fermeture (comme un JDialog modal)
    }

    // =========================================================
    //  Boutons (appelés par dialogue-film.fxml)
    // =========================================================
    @FXML
    private void onAnnuler() {
        fenetre.close();
    }

    @FXML
    private void onValider() {
        confirme = true;
        fenetre.close();
    }

    private void preRemplir(Film film) {
        champTitre.setText(film.getTitre());
        champDuree.setText(String.valueOf(film.getDuree()));
        champNote.setText(String.valueOf(film.getNote()));
        champSynopsis.setText(film.getSynopsis());
        for (String genre : comboGenre.getItems()) {
            if (genre.equalsIgnoreCase(film.getGenre())) {
                comboGenre.setValue(genre);
            }
        }
    }

    public boolean estConfirme() { return confirme; }
    public String getTitre()     { return champTitre.getText().trim(); }
    public String getGenre()     { return comboGenre.getValue(); }
    public String getDuree()     { return champDuree.getText().trim(); }
    public String getNote()      { return champNote.getText().trim(); }
    public String getSynopsis()  { return champSynopsis.getText().trim(); }
}
