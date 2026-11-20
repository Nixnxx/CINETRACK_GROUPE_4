package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Film;
import model.Salle;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/*
  DialogueSeanceControleur : contrôleur du formulaire dialogue-seance.fxml
  (ajout d'une séance, dans une fenêtre modale).
 */
public class DialogueSeanceControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de dialogue-seance.fxml)
    @FXML private ComboBox<String> comboFilm;
    @FXML private ComboBox<String> comboSalle;
    @FXML private TextField champDate;
    @FXML private TextField champHeure;
    @FXML private TextField champPlaces;
    @FXML private TextField champPrix;

    private Stage fenetre;
    private List<Film> films;
    private List<Salle> salles;
    private boolean confirme = false;

    // Charge le FXML, affiche la fenêtre et attend sa fermeture.
    public static DialogueSeanceControleur ouvrir(Window parent, List<Film> films, List<Salle> salles) {
        try {
            FXMLLoader chargeur = new FXMLLoader(
                    DialogueSeanceControleur.class.getResource("/view/dialogue-seance.fxml"));
            Parent racine = chargeur.load();
            DialogueSeanceControleur controleur = chargeur.getController();
            controleur.afficher(parent, racine, films, salles);
            return controleur;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger dialogue-seance.fxml", e);
        }
    }

    private void afficher(Window parent, Parent racine, List<Film> films, List<Salle> salles) {
        // On garde les listes : l'ordre des combos = l'ordre des listes
        this.films = films;
        this.salles = salles;

        for (Film f : films) {
            comboFilm.getItems().add(f.getIdFilm() + " - " + f.getTitre());
        }
        if (!films.isEmpty()) {
            comboFilm.getSelectionModel().selectFirst();
        }

        for (Salle s : salles) {
            comboSalle.getItems().add(s.getIdSalle() + " - " + s.getNom());
        }
        if (!salles.isEmpty()) {
            comboSalle.getSelectionModel().selectFirst();
        }

        champDate.setText(LocalDate.now().toString());

        fenetre = new Stage();
        fenetre.initOwner(parent);
        fenetre.initModality(Modality.WINDOW_MODAL);
        fenetre.setResizable(false);
        fenetre.setTitle("Ajouter une séance");

        Scene scene = new Scene(racine);
        fenetre.setScene(scene);
        fenetre.showAndWait();   // attend la fermeture (comme un JDialog modal)
    }

    // =========================================================
    //  Boutons (appelés par dialogue-seance.fxml)
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

    public boolean estConfirme() { return confirme; }

    public int getIdFilm() {
        int position = comboFilm.getSelectionModel().getSelectedIndex();
        if (position >= 0 && position < films.size()) {
            return films.get(position).getIdFilm();
        }
        return -1;
    }

    public int getIdSalle() {
        int position = comboSalle.getSelectionModel().getSelectedIndex();
        if (position >= 0 && position < salles.size()) {
            return salles.get(position).getIdSalle();
        }
        return -1;
    }

    public String getDate()   { return champDate.getText().trim(); }
    public String getHeure()  { return champHeure.getText().trim(); }
    public String getPlaces() { return champPlaces.getText().trim(); }
    public String getPrix()   { return champPrix.getText().trim(); }
}
