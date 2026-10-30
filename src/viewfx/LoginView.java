package viewfx;

import controleur.UtilisateurControleur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final UtilisateurControleur controleur = new UtilisateurControleur();
    private final Runnable surConnexionReussie;

    private final TextField champLogin = new TextField();
    private final PasswordField champMotDePasse = new PasswordField();
    private final Label labelErreur = new Label();
    private final Parent racine;

    public LoginView(Runnable surConnexionReussie) {
        this.surConnexionReussie = surConnexionReussie;
        this.racine = construire();
    }

    public Parent getRacine() {
        return racine;
    }

    private Parent construire() {
        Label titre = new Label("CinéTrack");
        titre.getStyleClass().add("titre");

        Label sousTitre = new Label("Connexion à l'application");
        sousTitre.getStyleClass().add("sous-titre");

        GridPane formulaire = new GridPane();
        formulaire.setHgap(10);
        formulaire.setVgap(12);
        formulaire.add(creerLibelle("Identifiant :"), 0, 0);
        formulaire.add(champLogin, 1, 0);
        formulaire.add(creerLibelle("Mot de passe :"), 0, 1);
        formulaire.add(champMotDePasse, 1, 1);
        GridPane.setHgrow(champLogin, Priority.ALWAYS);
        GridPane.setHgrow(champMotDePasse, Priority.ALWAYS);

        labelErreur.getStyleClass().add("erreur");
        labelErreur.setMinHeight(16);

        Button bouton = new Button("Se connecter");
        bouton.getStyleClass().add("bouton-principal");
        bouton.setMaxWidth(Double.MAX_VALUE);
        bouton.setPrefHeight(36);
        bouton.setOnAction(e -> tenterConnexion());
        champMotDePasse.setOnAction(e -> tenterConnexion());   // touche Entrée

        VBox carte = new VBox(12, titre, sousTitre, formulaire, labelErreur, bouton);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte");

        StackPane fond = new StackPane(carte);
        fond.setPadding(new Insets(20));
        return fond;
    }

    private Label creerLibelle(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("libelle");
        return l;
    }

    private void tenterConnexion() {
        String login = champLogin.getText().trim();
        String mdp = champMotDePasse.getText();

        // Le contrôleur vérifie en base, définit le rôle et écrit le log de connexion
        String erreur = controleur.connecter(login, mdp);
        if (erreur == null) {
            surConnexionReussie.run();
        } else {
            labelErreur.setText(erreur);
            champMotDePasse.clear();
        }
    }
}