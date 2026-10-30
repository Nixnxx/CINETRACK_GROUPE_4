package viewfx;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modele.Film;

public class DialogueFilmView {

    private static final String[] GENRES = {
            "Action", "Comédie", "Drame", "Animation",
            "Horreur", "Science-Fiction", "Thriller", "Romance", "Documentaire"
    };

    private final Stage fenetre = new Stage();
    private TextField champTitre;
    private ComboBox<String> comboGenre;
    private TextField champDuree;
    private TextField champNote;
    private TextArea champSynopsis;
    private boolean confirme = false;

    // filmExistant == null : ajout ; sinon : modification
    public DialogueFilmView(Stage parent, Film filmExistant) {
        fenetre.initOwner(parent);
        fenetre.initModality(Modality.WINDOW_MODAL);
        fenetre.setResizable(false);
        if (filmExistant == null) {
            fenetre.setTitle("Ajouter un film");
        } else {
            fenetre.setTitle("Modifier le film");
        }

        BorderPane racine = new BorderPane();
        racine.setCenter(creerFormulaire());
        racine.setBottom(creerBarreBoutons());

        if (filmExistant != null) {
            preRemplir(filmExistant);
        }

        Scene scene = new Scene(racine, 480, 440);
        Theme.appliquer(scene);
        fenetre.setScene(scene);
        fenetre.showAndWait();   // attend la fermeture (comme un JDialog modal)
    }

    private GridPane creerFormulaire() {
        GridPane grille = new GridPane();
        grille.getStyleClass().add("formulaire");
        grille.setHgap(10);
        grille.setVgap(12);

        champTitre = new TextField();

        comboGenre = new ComboBox<>();
        comboGenre.getItems().addAll(GENRES);
        comboGenre.setValue(GENRES[0]);
        comboGenre.setMaxWidth(Double.MAX_VALUE);

        champDuree = new TextField();
        champNote = new TextField();

        champSynopsis = new TextArea();
        champSynopsis.setWrapText(true);

        Label labelSynopsis = creerLabel("Synopsis :");
        GridPane.setValignment(labelSynopsis, VPos.TOP);

        grille.add(creerLabel("Titre :"), 0, 0);
        grille.add(champTitre, 1, 0);
        grille.add(creerLabel("Genre :"), 0, 1);
        grille.add(comboGenre, 1, 1);
        grille.add(creerLabel("Durée (min) :"), 0, 2);
        grille.add(champDuree, 1, 2);
        grille.add(creerLabel("Note (0–10) :"), 0, 3);
        grille.add(champNote, 1, 3);
        grille.add(labelSynopsis, 0, 4);
        grille.add(champSynopsis, 1, 4);

        // La colonne des champs prend toute la largeur disponible
        ColumnConstraints colLabels = new ColumnConstraints();
        ColumnConstraints colChamps = new ColumnConstraints();
        colChamps.setHgrow(Priority.ALWAYS);
        grille.getColumnConstraints().addAll(colLabels, colChamps);

        // Le synopsis prend toute la hauteur restante
        GridPane.setVgrow(champSynopsis, Priority.ALWAYS);

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

    private void preRemplir(Film film) {
        champTitre.setText(film.getTitre());
        champDuree.setText(String.valueOf(film.getDuree()));
        champNote.setText(String.valueOf(film.getNote()));
        champSynopsis.setText(film.getSynopsis());
        for (String genre : GENRES) {
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