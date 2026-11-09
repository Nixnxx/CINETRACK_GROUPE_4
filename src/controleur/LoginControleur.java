package controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.UtilisateurService;

import java.sql.SQLException;

/*
  LoginControleur : contrôleur de l'écran login.fxml.
  Une fois la connexion réussie, il appelle l'action donnée par MainFx
  (qui ouvre la fenêtre principale).
 */
public class LoginControleur {

    // Éléments de l'écran (les noms correspondent aux fx:id de login.fxml)
    @FXML private TextField champLogin;
    @FXML private PasswordField champMotDePasse;
    @FXML private Label labelErreur;

    private final UtilisateurService service = new UtilisateurService();
    private Runnable surConnexionReussie;

    // Appelée par MainFx juste après le chargement du FXML
    public void setSurConnexionReussie(Runnable surConnexionReussie) {
        this.surConnexionReussie = surConnexionReussie;
    }

    @FXML
    private void onConnecter() {
        String login = champLogin.getText().trim();
        String mdp = champMotDePasse.getText();

        // Le service vérifie en base, définit le rôle et écrit le log de connexion
        String erreur = connecter(login, mdp);
        if (erreur == null) {
            surConnexionReussie.run();
        } else {
            labelErreur.setText(erreur);
            champMotDePasse.clear();
        }
    }

    /** @return null si la connexion réussit, sinon le message d'erreur à afficher. */
    private String connecter(String login, String motDePasse) {
        try {
            return service.connecter(login, motDePasse);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Base de données inaccessible.";
        }
    }
}
