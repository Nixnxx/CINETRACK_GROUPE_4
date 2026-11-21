package controller;

import dao.LogDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;

/*
  PrincipaleControleur : contrôleur de la fenêtre principale (principale.fxml).
  Il affiche le bon rôle, cache la section Administration aux gestionnaires,
  charge les écrans FXML dans la zone centrale et passe de l'un à l'autre.
 */
public class PrincipaleControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de principale.fxml)
    @FXML private Label badge;
    @FXML private Label labelStatut;
    @FXML private Label sousTitreAccueil;
    @FXML private StackPane zoneCentrale;
    @FXML private VBox ecranAccueil;
    @FXML private VBox sectionAdmin;
    @FXML private Button btnFilms;
    @FXML private Button btnSeances;
    @FXML private Button btnBilletterie;
    @FXML private Button btnLogs;
    @FXML private Button btnUtilisateurs;
    @FXML private Button btnClients;

    private String role;
    private Button btnActif = null;

    // Un écran par section (comme le CardLayout en Swing)
    private Pane ecranFilms;
    private Pane ecranSeances;
    private Pane ecranBilletterie;
    private Pane ecranLogs;           // admin uniquement
    private Pane ecranUtilisateurs;   // admin uniquement
    private Pane ecranClients;         // admin uniquement

    private FilmControleur controleurFilms;
    private SeanceControleur controleurSeances;
    private BilletControleur controleurBilletterie;
    private LogControleur controleurLogs;                    // admin uniquement
    private UtilisateurControleur controleurUtilisateurs;    // admin uniquement
    private ClientControleur controleurClients;              // admin uniquement

    // Appelée automatiquement par JavaFX une fois le FXML chargé
    @FXML
    public void initialize() {
        role = LogDAO.getRoleActuel();

        // Badge du rôle
        badge.setText(role.toUpperCase());
        if (estAdmin()) {
            badge.getStyleClass().add("badge-admin");
        } else {
            badge.getStyleClass().add("badge-gestionnaire");
        }

        // Texte de l'écran d'accueil
        String texte = "Connecté en tant que : " + role;
        if (estAdmin()) {
            texte = texte + "  —  Accès complet";
            sousTitreAccueil.getStyleClass().add("role-admin");
        } else {
            texte = texte + "  —  Accès standard";
            sousTitreAccueil.getStyleClass().add("role-gestionnaire");
        }
        sousTitreAccueil.setText(texte);

        // La section Administration n'existe que pour l'admin
        sectionAdmin.setVisible(estAdmin());
        sectionAdmin.setManaged(estAdmin());

        // Chargement des écrans décrits en FXML
        FXMLLoader chargeur = chargerFxml("films.fxml");
        ecranFilms = chargeur.getRoot();
        controleurFilms = chargeur.getController();

        chargeur = chargerFxml("seances.fxml");
        ecranSeances = chargeur.getRoot();
        controleurSeances = chargeur.getController();

        chargeur = chargerFxml("billetterie.fxml");
        ecranBilletterie = chargeur.getRoot();
        controleurBilletterie = chargeur.getController();

        zoneCentrale.getChildren().addAll(ecranFilms, ecranSeances, ecranBilletterie);

        if (estAdmin()) {
            chargeur = chargerFxml("logs.fxml");
            ecranLogs = chargeur.getRoot();
            controleurLogs = chargeur.getController();

            chargeur = chargerFxml("utilisateurs.fxml");
            ecranUtilisateurs = chargeur.getRoot();
            controleurUtilisateurs = chargeur.getController();

            chargeur = chargerFxml("clients.fxml");
            ecranClients = chargeur.getRoot();
            controleurClients = chargeur.getController();

            zoneCentrale.getChildren().addAll(ecranLogs, ecranUtilisateurs, ecranClients);
        }

        afficherVue("ACCUEIL");
        mettreAJourStatut("CinéTrack prêt  |  Rôle : " + role);
    }

    private boolean estAdmin() {
        return "admin".equals(role);
    }

    // Charge un écran décrit en FXML : le FXMLLoader crée le contrôleur (fx:controller).
    // On récupère ensuite l'écran avec getRoot() et le contrôleur avec getController().
    private FXMLLoader chargerFxml(String fichier) {
        try {
            FXMLLoader chargeur = new FXMLLoader(getClass().getResource("/view/" + fichier));
            chargeur.load();
            return chargeur;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger " + fichier, e);
        }
    }

    // =========================================================
    //  Boutons du menu de gauche (appelés par principale.fxml)
    // =========================================================
    @FXML
    private void onFilms() {
        afficherVue("FILMS");
        setActif(btnFilms);
    }

    @FXML
    private void onSeances() {
        afficherVue("SEANCES");
        setActif(btnSeances);
    }

    @FXML
    private void onBilletterie() {
        afficherVue("BILLETTERIE");
        setActif(btnBilletterie);
    }

    @FXML
    private void onLogs() {
        afficherVue("LOGS");
        setActif(btnLogs);
    }

    @FXML
    private void onUtilisateurs() {
        afficherVue("UTILISATEURS");
        setActif(btnUtilisateurs);
    }

    @FXML
    private void onClients() {
        afficherVue("CLIENTS");
        setActif(btnClients);
    }

    @FXML
    private void onRechercheGenre() {
        afficherVue("FILMS");
        setActif(btnFilms);
        controleurFilms.ouvrirRechercheGenre();
    }

    @FXML
    private void onPlanning() {
        afficherVue("SEANCES");
        setActif(btnSeances);
        controleurSeances.chargerPlanningAujourdhui();
    }

    @FXML
    private void onAPropos() {
        DialogueMessageControleur.info(fenetre(), "À propos",
                "CinéTrack v1.0\nApplication de gestion de cinéma\nBTS SIO SLAM — 1ère année");
    }

    // =========================================================
    //  Navigation
    // =========================================================
    // Met en évidence le bouton du menu choisi
    private void setActif(Button btn) {
        if (btnActif != null) {
            btnActif.getStyleClass().remove("actif");
        }
        btnActif = btn;
        btn.getStyleClass().add("actif");
    }

    // Affiche un seul écran et cache les autres
    public void afficherVue(String nomVue) {
        if (nomVue.equals("LOGS") && controleurLogs != null) {
            controleurLogs.rafraichir();            // pour voir les logs les plus récents
        }
        if (nomVue.equals("CLIENTS") && controleurClients != null) {
            controleurClients.rafraichir();         // achats récents à jour
        }
        if (nomVue.equals("BILLETTERIE") && controleurBilletterie != null) {
            controleurBilletterie.rafraichir();     // séances à jour à chaque ouverture
        }
        montrerEcran(ecranAccueil, nomVue.equals("ACCUEIL"));
        montrerEcran(ecranFilms, nomVue.equals("FILMS"));
        montrerEcran(ecranSeances, nomVue.equals("SEANCES"));
        montrerEcran(ecranBilletterie, nomVue.equals("BILLETTERIE"));
        montrerEcran(ecranLogs, nomVue.equals("LOGS"));
        montrerEcran(ecranUtilisateurs, nomVue.equals("UTILISATEURS"));
        montrerEcran(ecranClients, nomVue.equals("CLIENTS"));
    }

    private void montrerEcran(Pane ecran, boolean visible) {
        if (ecran != null) {          // null si l'écran est réservé à l'admin
            ecran.setVisible(visible);
            ecran.setManaged(visible);
        }
    }

    public void mettreAJourStatut(String message) {
        labelStatut.setText(message);
    }

    // Fenêtre propriétaire des boîtes de dialogue (null tant que l'écran n'est pas affiché)
    private Window fenetre() {
        if (labelStatut.getScene() == null) {
            return null;
        }
        return labelStatut.getScene().getWindow();
    }
}
