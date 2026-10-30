package viewfx;

import controleur.FilmControleur;
import controleur.FilmVue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.Film;

import java.util.List;

public class FilmsView implements FilmVue {

    private static final String[] GENRES_FILTRE = {
            "Tous", "Action", "Comédie", "Drame", "Animation",
            "Horreur", "Science-Fiction", "Thriller", "Romance", "Documentaire"
    };

    private final Stage fenetre;               // fenêtre propriétaire des boîtes de dialogue
    private final FilmControleur controleur;
    private final BorderPane racine = new BorderPane();

    private TableView<Film> tableFilms;
    private final ObservableList<Film> donnees = FXCollections.observableArrayList();
    private ComboBox<String> comboFiltreGenre;
    private Button btnAjouter, btnModifier, btnSupprimer, btnRafraichir;
    private Label labelNbFilms;
    private TextArea textSynopsis;

    public FilmsView(Stage fenetre) {
        this.fenetre = fenetre;
        this.controleur = new FilmControleur(this);

        racine.setTop(creerBandeauNord());
        racine.setCenter(creerZoneCentrale());
        racine.setBottom(creerPanneauBoutons());

        // Le filtre est activé seulement maintenant, quand tous les composants existent
        comboFiltreGenre.setOnAction(e -> filtrerParGenre());

        controleur.chargerFilms();
    }

    public BorderPane getRacine() {
        return racine;
    }

    // =========================================================
    //  Construction de l'écran
    // =========================================================
    private HBox creerBandeauNord() {
        labelNbFilms = new Label("Films");
        labelNbFilms.getStyleClass().add("titre-section");

        Label labelGenre = new Label("Genre :");
        labelGenre.getStyleClass().add("libelle");

        comboFiltreGenre = new ComboBox<>();
        comboFiltreGenre.getItems().addAll(GENRES_FILTRE);
        comboFiltreGenre.setValue("Tous");

        HBox filtre = new HBox(8);
        filtre.setAlignment(Pos.CENTER_RIGHT);
        filtre.getChildren().addAll(labelGenre, comboFiltreGenre);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox bandeau = new HBox();
        bandeau.setAlignment(Pos.CENTER);
        bandeau.getStyleClass().add("bandeau");
        bandeau.getChildren().addAll(labelNbFilms, espace, filtre);
        return bandeau;
    }

    private SplitPane creerZoneCentrale() {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.VERTICAL);
        split.getItems().addAll(creerTable(), creerPanneauSynopsis());
        split.setDividerPositions(0.75);
        return split;
    }

    private TableView<Film> creerTable() {
        tableFilms = new TableView<>();
        tableFilms.setItems(donnees);
        tableFilms.setPlaceholder(new Label("Aucun film"));

        TableColumn<Film, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.valueOf(ligne.getValue().getIdFilm())));
        colId.setPrefWidth(60);

        TableColumn<Film, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getTitre()));
        colTitre.setPrefWidth(300);

        TableColumn<Film, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getGenre()));
        colGenre.setPrefWidth(150);

        TableColumn<Film, String> colDuree = new TableColumn<>("Durée");
        colDuree.setCellValueFactory(ligne ->
                new SimpleStringProperty(ligne.getValue().getDureeFormatee()));
        colDuree.setPrefWidth(100);

        TableColumn<Film, String> colNote = new TableColumn<>("Note");
        colNote.setCellValueFactory(ligne ->
                new SimpleStringProperty(String.format("%.1f", ligne.getValue().getNote())));
        colNote.setPrefWidth(80);

        tableFilms.getColumns().addAll(colId, colTitre, colGenre, colDuree, colNote);

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

        return tableFilms;
    }

    private VBox creerPanneauSynopsis() {
        Label titre = new Label("Synopsis");
        titre.getStyleClass().add("libelle");

        textSynopsis = new TextArea();
        textSynopsis.setEditable(false);
        textSynopsis.setWrapText(true);
        VBox.setVgrow(textSynopsis, Priority.ALWAYS);

        VBox panneau = new VBox(5);
        panneau.getStyleClass().add("panneau-synopsis");
        panneau.getChildren().addAll(titre, textSynopsis);
        return panneau;
    }

    private HBox creerPanneauBoutons() {
        btnAjouter = new Button("＋ Ajouter");
        btnAjouter.getStyleClass().add("bouton-vert");

        btnModifier = new Button("✎ Modifier");
        btnModifier.getStyleClass().add("bouton-bleu");

        btnSupprimer = new Button("✕ Supprimer");
        btnSupprimer.getStyleClass().add("bouton-rouge");

        btnRafraichir = new Button("↺ Rafraîchir");
        btnRafraichir.getStyleClass().add("bouton-gris");

        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        btnAjouter.setOnAction(e -> actionAjouter());
        btnModifier.setOnAction(e -> actionModifier());
        btnSupprimer.setOnAction(e -> actionSupprimer());
        btnRafraichir.setOnAction(e -> controleur.chargerFilms());

        Region espace = new Region();
        espace.setPrefWidth(16);

        HBox panneau = new HBox(8);
        panneau.setAlignment(Pos.CENTER_LEFT);
        panneau.getStyleClass().add("barre-boutons");
        panneau.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, espace, btnRafraichir);
        return panneau;
    }

    // =========================================================
    //  Actions
    // =========================================================
    private void actionAjouter() {
        DialogueFilmView d = new DialogueFilmView(fenetre, null);
        if (d.estConfirme()) {
            controleur.ajouterFilm(d.getTitre(), d.getGenre(), d.getDuree(), d.getNote(), d.getSynopsis());
        }
    }

    private void actionModifier() {
        Film film = tableFilms.getSelectionModel().getSelectedItem();
        if (film == null) {
            return;
        }
        DialogueFilmView d = new DialogueFilmView(fenetre, film);
        if (d.estConfirme()) {
            controleur.modifierFilm(film.getIdFilm(), d.getTitre(), d.getGenre(),
                    d.getDuree(), d.getNote(), d.getSynopsis());
        }
    }

    private void actionSupprimer() {
        Film film = tableFilms.getSelectionModel().getSelectedItem();
        if (film == null) {
            return;
        }
        controleur.supprimerFilm(film.getIdFilm(), film.getTitre());
    }

    private void filtrerParGenre() {
        String genre = comboFiltreGenre.getValue();
        if ("Tous".equals(genre)) {
            controleur.chargerFilms();
        } else {
            controleur.rechercherParGenre(genre);
        }
    }

    // =========================================================
    //  Méthodes appelées par le contrôleur (interface FilmVue)
    // =========================================================
    @Override
    public void afficherFilms(List<Film> films) {
        donnees.setAll(films);
        textSynopsis.setText("");
        mettreAJourTitre(films.size());
    }

    @Override
    public void afficherSucces(String message) {
        Dialogues.info(fenetre, "Succès", message);
    }

    @Override
    public void afficherErreur(String message) {
        Dialogues.erreur(fenetre, message);
    }

    @Override
    public boolean demanderConfirmation(String message) {
        return Dialogues.confirmer(fenetre, message);
    }

    public void mettreAJourTitre(int nombre) {
        labelNbFilms.setText("Films (" + nombre + ")");
    }

    // Appelée par le bouton « Rechercher par genre » de la sidebar
    public void ouvrirRechercheGenre() {
        comboFiltreGenre.requestFocus();
        comboFiltreGenre.show();
    }
}