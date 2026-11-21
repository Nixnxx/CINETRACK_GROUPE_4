package dao;

import model.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface UtilisateurDAO {
    Utilisateur authentifier(String login, String motDePasse) throws SQLException;

    List<Utilisateur> findGestionnaires() throws SQLException;

    boolean loginExiste(String login) throws SQLException;

    int insererGestionnaire(String login, String motDePasse) throws SQLException;

    boolean supprimerGestionnaire(int idUtilisateur) throws SQLException;

    // ----- Anti force brute -----

    // Minutes restantes avant la fin du blocage (0 si le compte n'est pas bloqué)
    int minutesRestantesBlocage(String login) throws SQLException;

    // Ajoute 1 au nombre d'échecs et retourne le nouveau total (0 si le login n'existe pas)
    int enregistrerEchec(String login) throws SQLException;

    // Bloque le compte pendant un certain nombre de minutes (le compteur repart à 0)
    void bloquer(String login, int minutes) throws SQLException;

    // Après une connexion réussie : compteur à 0, plus de blocage
    void reinitialiserTentatives(String login) throws SQLException;
}
