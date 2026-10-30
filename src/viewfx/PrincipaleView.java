package viewfx;

import dao.LogDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrincipaleView {

    private final Stage stage;
    private final BorderPane racine = new BorderPane();
    private final String role;

    // Zone centrale : un écran par section (comme le CardLayout en Swing)
    private StackPane zoneCentrale;
    private Pane ecranAccueil;
    private Pane ecranFilms;
    private Pane ecranSeances;
    private Pane ecranLogs;           // admin uniquement
    private Pane ecranUtilisateurs;   // admin uniquement

    private FilmsView vueFilms;
    private SeancesView vueSeances;
    private LogsView vueLogs;                    // admin uniquement
    private UtilisateursView vueUtilisateurs;    // admin uniquement

    private Label labelStatut;
    private Button btnActif = null;

    public PrincipaleView(Stage stage) {
        this.stage = stage;
        this.role = LogDAO.getRoleActuel();

        racine.setTop(creerBarreHaute());
        racine.setLeft(creerSidebar());
        racine.setCenter(creerZoneCentrale());
        racine.setBottom(creerBarreStatut());

        afficherVue("ACCUEIL");
        mettreAJourStatut("CinéTrack prêt  |  Rôle : " + role);
    }

    public Parent getRacine() {
        return racine;
    }

    private boolean estAdmin() {
        return "admin".equals(role);
    }

    // =========================================================
    //  BARRE DU HAUT : nom de l'application, menus, badge du rôle
    // =========================================================
    private HBox creerBarreHaute() {
        // Partie gauche
        Label point = creerLabel("●", "point");
        Label nom = creerLabel("CinéTrack", "nom-app");
        Label trait = creerLabel("|", "separateur-texte");
        Label sousNom = creerLabel("Le Lumière", "sous-nom");

        HBox gauche = new HBox(14);
        gauche.setAlignment(Pos.CENTER_LEFT);
        gauche.getChildren().addAll(point, nom, trait, sousNom);

        // Partie droite : menus
        MenuBar menus = new MenuBar();

        if (estAdmin()) {
            Menu menuAdmin = new Menu("⚙  Administration");
            menuAdmin.getStyleClass().add("menu-admin");

            MenuItem itemLogs = new MenuItem("Consulter les logs");
            itemLogs.setOnAction(e -> {
                afficherVue("LOGS");
                desactiverBoutonsSidebar();
            });

            MenuItem itemUtilisateurs = new MenuItem("Gérer les gestionnaires");
            itemUtilisateurs.setOnAction(e -> {
                afficherVue("UTILISATEURS");
                desactiverBoutonsSidebar();
            });

            menuAdmin.getItems().addAll(itemLogs, itemUtilisateurs);
            menus.getMenus().add(menuAdmin);
        }

        Menu menuAPropos = new Menu("À propos");
        MenuItem itemAPropos = new MenuItem("À propos de CinéTrack");
        itemAPropos.setOnAction(e -> Dialogues.info(stage, "À propos",
                "CinéTrack v1.0\nApplication de gestion de cinéma\nBTS SIO SLAM — 1ère année"));
        menuAPropos.getItems().add(itemAPropos);
        menus.getMenus().add(menuAPropos);

        // Badge du rôle
        Label badge = creerLabel(role.toUpperCase(), "badge");
        if (estAdmin()) {
            badge.getStyleClass().add("badge-admin");
        } else {
            badge.getStyleClass().add("badge-gestionnaire");
        }

        HBox droite = new HBox(8);
        droite.setAlignment(Pos.CENTER_RIGHT);
        droite.getChildren().addAll(menus, badge);

        // Espace flexible entre la gauche et la droite
        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox barre = new HBox();
        barre.setAlignment(Pos.CENTER);
        barre.getStyleClass().add("barre-haute");
        barre.getChildren().addAll(gauche, espace, droite);
        return barre;
    }

    // =========================================================
    //  SIDEBAR GAUCHE
    // =========================================================
    private VBox creerSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);
        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(200);

        // Section navigation
        sidebar.getChildren().add(creerLabel("NAVIGATION", "section-label"));

        Button btnFilms = creerBoutonSidebar("🎬  Films", "FILMS");
        Button btnSeances = creerBoutonSidebar("🕐  Séances", "SEANCES");
        sidebar.getChildren().add(btnFilms);
        sidebar.getChildren().add(btnSeances);

        // Sous-action Films
        Button sousGenre = creerSousBouton("Rechercher par genre");
        sousGenre.setOnAction(e -> {
            afficherVue("FILMS");
            setActif(btnFilms);
            vueFilms.ouvrirRechercheGenre();
        });
        sidebar.getChildren().add(sousGenre);

        // Séparateur
        Region separateur = new Region();
        separateur.getStyleClass().add("separateur");
        separateur.setMinHeight(1);
        separateur.setPrefHeight(1);
        separateur.setMaxHeight(1);
        VBox.setMargin(separateur, new Insets(10, 0, 10, 0));
        sidebar.getChildren().add(separateur);

        // Section accès rapide
        sidebar.getChildren().add(creerLabel("ACCÈS RAPIDE", "section-label"));

        Button sousPlanning = creerSousBouton("Planning du jour");
        sousPlanning.setOnAction(e -> {
            afficherVue("SEANCES");
            setActif(btnSeances);
            vueSeances.chargerPlanningAujourdhui();
        });
        sidebar.getChildren().add(sousPlanning);

        // Pousse la version tout en bas
        Region poussoir = new Region();
        VBox.setVgrow(poussoir, Priority.ALWAYS);
        sidebar.getChildren().add(poussoir);

        Label version = creerLabel("v1.0  —  BTS SIO SLAM", "version");
        version.setMaxWidth(Double.MAX_VALUE);
        version.setAlignment(Pos.CENTER);
        VBox.setMargin(version, new Insets(0, 0, 12, 0));
        sidebar.getChildren().add(version);

        return sidebar;
    }

    private Button creerBoutonSidebar(String texte, String vue) {
        Button btn = new Button(texte);
        btn.getStyleClass().add("nav-bouton");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            afficherVue(vue);
            setActif(btn);
        });
        return btn;
    }

    private Button creerSousBouton(String texte) {
        Button btn = new Button("› " + texte);
        btn.getStyleClass().add("sous-bouton");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void setActif(Button btn) {
        desactiverBoutonsSidebar();
        btnActif = btn;
        btn.getStyleClass().add("actif");
    }

    private void desactiverBoutonsSidebar() {
        if (btnActif != null) {
            btnActif.getStyleClass().remove("actif");
            btnActif = null;
        }
    }

    // =========================================================
    //  ZONE CENTRALE : un écran par section
    // =========================================================
    private StackPane creerZoneCentrale() {
        zoneCentrale = new StackPane();

        ecranAccueil = creerEcranAccueil();

        vueFilms = new FilmsView(stage);
        ecranFilms = vueFilms.getRacine();

        vueSeances = new SeancesView(stage);
        ecranSeances = vueSeances.getRacine();

        zoneCentrale.getChildren().addAll(ecranAccueil, ecranFilms, ecranSeances);

        if (estAdmin()) {
            vueLogs = new LogsView(stage);
            ecranLogs = vueLogs.getRacine();

            vueUtilisateurs = new UtilisateursView(stage);
            ecranUtilisateurs = vueUtilisateurs.getRacine();
            zoneCentrale.getChildren().addAll(ecranLogs, ecranUtilisateurs);
        }

        return zoneCentrale;
    }

    private VBox creerEcranAccueil() {
        Label titre = creerLabel("Bienvenue dans CinéTrack", "titre-accueil");

        String texte = "Connecté en tant que : " + role;
        if (estAdmin()) {
            texte = texte + "  —  Accès complet";
        } else {
            texte = texte + "  —  Accès standard";
        }
        Label sousTitre = creerLabel(texte, "sous-titre-accueil");
        if (estAdmin()) {
            sousTitre.getStyleClass().add("role-admin");
        } else {
            sousTitre.getStyleClass().add("role-gestionnaire");
        }

        Label indication = creerLabel("Sélectionnez une section dans le menu à gauche.", "indication");

        VBox ecran = new VBox(10);
        ecran.setAlignment(Pos.CENTER);
        ecran.getChildren().addAll(titre, sousTitre, indication);
        return ecran;
    }

    // Affiche un seul écran et cache les autres
    public void afficherVue(String nomVue) {
        if (nomVue.equals("LOGS") && vueLogs != null) {
            vueLogs.rafraichir();     // pour voir les logs les plus récents
        }
        montrerEcran(ecranAccueil, nomVue.equals("ACCUEIL"));
        montrerEcran(ecranFilms, nomVue.equals("FILMS"));
        montrerEcran(ecranSeances, nomVue.equals("SEANCES"));
        montrerEcran(ecranLogs, nomVue.equals("LOGS"));
        montrerEcran(ecranUtilisateurs, nomVue.equals("UTILISATEURS"));
    }

    private void montrerEcran(Pane ecran, boolean visible) {
        if (ecran != null) {          // null si l'écran est réservé à l'admin
            ecran.setVisible(visible);
            ecran.setManaged(visible);
        }
    }

    // =========================================================
    //  BARRE DE STATUT
    // =========================================================
    private HBox creerBarreStatut() {
        labelStatut = creerLabel("", "statut");

        HBox barre = new HBox();
        barre.getStyleClass().add("barre-statut");
        barre.getChildren().add(labelStatut);
        return barre;
    }

    public void mettreAJourStatut(String message) {
        labelStatut.setText(message);
    }

    // =========================================================
    //  Outil : créer un Label avec une classe CSS
    // =========================================================
    private Label creerLabel(String texte, String classeCss) {
        Label label = new Label(texte);
        label.getStyleClass().add(classeCss);
        return label;
    }
}
