package viewfx;

import controleur.SeanceControleur;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modele.Film;
import modele.Salle;

import java.time.LocalDate;
import java.util.List;

public class DialogueSeanceView {

    private final Stage fenetre = new Stage();
    private final List<Film> films;
    private final List<Salle> salles;

    private ComboBox<String> comboFilm;
    private ComboBox<String> comboSalle;
    private TextField champDate;
    private TextField champHeure;
    private TextField champPlaces;
    private boolean confirme = false;

    public DialogueSeanceView(Stage parent, SeanceControleur controleur) {
        // On charge les listes une seule fois : l'ordre des combos = l'ordre des listes
        this.films = controleur.getTousFilms();
        this.salles = controleur.getToutesSalles();

        fenetre.initOwner(parent);
        fenetre.initModality(Modality.WINDOW_MODAL);
        fenetre.setResizable(false);
        fenetre.setTitle("Ajouter une séance");

        BorderPane racine = new BorderPane();
        racine.setCenter(creerFormulaire());
        racine.setBottom(creerBarreBoutons());

        Scene scene = new Scene(racine, 460, 340);
        Theme.appliquer(scene);
        fenetre.setScene(scene);
        fenetre.showAndWait();   // attend la fermeture (comme un JDialog modal)
    }

    private GridPane creerFormulaire() {
        GridPane grille = new GridPane();
        grille.getStyleClass().add("formulaire");
        grille.setHgap(10);
        grille.setVgap(12);

        comboFilm = new ComboBox<>();
        for (Film f : films) {
            comboFilm.getItems().add(f.getIdFilm() + " - " + f.getTitre());
        }
        if (!films.isEmpty()) {
            comboFilm.getSelectionModel().selectFirst();
        }
        comboFilm.setMaxWidth(Double.MAX_VALUE);

        comboSalle = new ComboBox<>();
        for (Salle s : salles) {
            comboSalle.getItems().add(s.getIdSalle() + " - " + s.getNom());
        }
        if (!salles.isEmpty()) {
            comboSalle.getSelectionModel().selectFirst();
        }
        comboSalle.setMaxWidth(Double.MAX_VALUE);

        champDate = new TextField(LocalDate.now().toString());
        champHeure = new TextField("20:00");
        champPlaces = new TextField("100");

        grille.add(creerLabel("Film :"), 0, 0);
        grille.add(comboFilm, 1, 0);
        grille.add(creerLabel("Salle :"), 0, 1);
        grille.add(comboSalle, 1, 1);
        grille.add(creerLabel("Date (AAAA-MM-JJ) :"), 0, 2);
        grille.add(champDate, 1, 2);
        grille.add(creerLabel("Heure (HH:MM) :"), 0, 3);
        grille.add(champHeure, 1, 3);
        grille.add(creerLabel("Places disponibles :"), 0, 4);
        grille.add(champPlaces, 1, 4);

        // La colonne des champs prend toute la largeur disponible
        ColumnConstraints colLabels = new ColumnConstraints();
        ColumnConstraints colChamps = new ColumnConstraints();
        colChamps.setHgrow(Priority.ALWAYS);
        grille.getColumnConstraints().addAll(colLabels, colChamps);

        return grille;
    }

    private HBox creerBarreBoutons() {
        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.getStyleClass().add("bouton-gris");
        btnAnnuler.setPrefWidth(90);
        btnAnnuler.setOnAction(e -> fenetre.close());

        Button btnValider = new Button("Valider");
        btnValider.getStyleClass().add("bouton-principal");
        btnValider.setPrefWidth(90);
        btnValider.setOnAction(e -> {
            confirme = true;
            fenetre.close();
        });

        HBox barre = new HBox(8);
        barre.setAlignment(Pos.CENTER_RIGHT);
        barre.getStyleClass().add("barre-dialogue");
        barre.getChildren().addAll(btnAnnuler, btnValider);
        return barre;
    }

    private Label creerLabel(String texte) {
        Label label = new Label(texte);
        label.getStyleClass().add("libelle");
        return label;
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
}
