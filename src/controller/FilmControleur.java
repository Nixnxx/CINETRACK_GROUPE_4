package controller;

import exceptions.FilmIntrouvableException;
import exceptions.NoteInvalideException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Film;
import service.FilmService;
import view.DialogueFilmView;
import view.Dialogues;

import java.util.List;

/*
  FilmControleur : contrôleur de l'écran films.fxml.
  JavaFX le crée tout seul (fx:controller), remplit les champs @FXML,
  puis appelle initialize(). Les boutons appellent les méthodes onXxx().
 */
public class FilmControleur {

    private static final String[] GENRES_FILTRE = {
            "Tous", "Action", "Comédie", "Drame", "Animation",
            "Horreur", "Science-Fiction", "Thriller", "Romance", "Documentaire"
    };

    // Éléments de l'écran (les noms correspondent aux fx:id de films.fxml)
    @FXML private Label labelNbFilms;
    @FXML private ComboBox<String> comboFiltreGenre;
    @FXML private TableView<Film> tableFilms;
    @FXML private TableColumn<Film, String> colId;
    @FXML private TableColumn<Film, String> colTitre;
    @FXML private TableColumn<Film, String> colGenre;
    @FXML private TableColumn<Film, String> colDuree;
    @FXML private TableColumn<Film, String> colNote;
    @FXML private TextArea textSynopsis;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    private final FilmService service = new FilmService();
    private final ObservableList<Film> donnees = FXCollections.observableArrayList();

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdFilm())));
        colTitre.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTitre()));
        colGenre.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getGenre()));
        colDuree.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDureeFormatee()));
        colNote.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.format("%.1f", ligne.getValue().getNote())));
        tableFilms.setItems(donnees);

        // Quand on sélectionne une ligne : activer les boutons et afficher le synopsis
        tableFilms.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            boolean selection = (nouveau != null);
            btnModifier.setDisable(!selection);
            btnSupprimer.setDisable(!selection);

            if (selection) {
                String synopsis = nouveau.getSynopsis();
                if (synopsis != null && !synopsis.isBlank()) {
                    textSynopsis.setText(synopsis);
                } else {
                    textSynopsis.setText("(Aucun synopsis disponible)");
                }
            } else {
                textSynopsis.setText("");
            }
        });

        comboFiltreGenre.getItems().addAll(GENRES_FILTRE);
        comboFiltreGenre.setValue("Tous");
        // Le filtre n'est activé qu'une fois la valeur de départ posée
        comboFiltreGenre.setOnAction(e -> rechercherParGenre(comboFiltreGenre.getValue()));

        chargerFilms();
    }

    // =========================================================
    //  Actions des boutons (appelées par films.fxml)
    // =========================================================
    @FXML
    private void onAjouter() {
        DialogueFilmView d = new DialogueFilmView((Stage) fenetre(), null);
        if (d.estConfirme()) {
            ajouterFilm(d.getTitre(), d.getGenre(), d.getDuree(), d.getNote(), d.getSynopsis());
        }
    }

    @FXML
    private void onModifier() {
        Film film = tableFilms.getSelectionModel().getSelectedItem();
        if (film == null) {
            return;
        }
        DialogueFilmView d = new DialogueFilmView((Stage) fenetre(), film);
        if (d.estConfirme()) {
            modifierFilm(film.getIdFilm(), d.getTitre(), d.getGenre(),
                    d.getDuree(), d.getNote(), d.getSynopsis());
        }
    }

    @FXML
    private void onSupprimer() {
        Film film = tableFilms.getSelectionModel().getSelectedItem();
        if (film == null) {
            return;
        }
        supprimerFilm(film.getIdFilm(), film.getTitre());
    }

    @FXML
    private void onRafraichir() {
        chargerFilms();
    }

    // =========================================================
    //  Logique de l'écran
    // =========================================================
    public void chargerFilms() {
        afficherFilms(service.getTousLesFilms());
    }

    public void ajouterFilm(String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);

            if (service.ajouterFilm(titre, genre, duree, note, synopsis)) {
                afficherSucces("Film ajouté avec succès !");
                chargerFilms();
            } else {
                afficherErreur("L'ajout du film a échoué.");
            }
        } catch (NumberFormatException e) {
            afficherErreur("Durée ou note invalide (format numérique requis).");
        } catch (NoteInvalideException | IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    public void modifierFilm(int id, String titre, String genre, String dureeStr, String noteStr, String synopsis) {
        try {
            int duree = Integer.parseInt(dureeStr);
            double note = Double.parseDouble(noteStr);

            if (service.modifierFilm(id, titre, genre, duree, note, synopsis)) {
                afficherSucces("Film modifié avec succès !");
                chargerFilms();
            } else {
                afficherErreur("La modification du film a échoué.");
            }
        } catch (NumberFormatException e) {
            afficherErreur("Durée ou note invalide.");
        } catch (FilmIntrouvableException | NoteInvalideException | IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    public void supprimerFilm(int id, String titre) {
        if (Dialogues.confirmer(fenetre(), "Supprimer le film \"" + titre + "\" ?")) {
            try {
                if (service.supprimerFilm(id)) {
                    afficherSucces("Film supprimé !");
                    chargerFilms();
                } else {
                    afficherErreur("La suppression du film a échoué.");
                }
            } catch (FilmIntrouvableException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    public void rechercherParGenre(String genre) {
        afficherFilms(service.rechercherParGenre(genre));
    }

    public Film getFilmParId(int id) {
        return service.getFilm(id);
    }

    // Appelée par le bouton « Rechercher par genre » de la sidebar
    public void ouvrirRechercheGenre() {
        comboFiltreGenre.requestFocus();
        comboFiltreGenre.show();
    }

    // =========================================================
    //  Affichage
    // =========================================================
    private void afficherFilms(List<Film> films) {
        donnees.setAll(films);
        textSynopsis.setText("");
        labelNbFilms.setText("Films (" + films.size() + ")");
    }

    private void afficherSucces(String message) {
        Dialogues.info(fenetre(), "Succès", message);
    }

    private void afficherErreur(String message) {
        Dialogues.erreur(fenetre(), message);
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (tableFilms.getScene() == null) {
            return null;
        }
        return tableFilms.getScene().getWindow();
    }
}