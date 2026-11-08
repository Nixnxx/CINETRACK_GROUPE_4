package dao;

import modele.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface UtilisateurDAO {
    Utilisateur authentifier(String login, String motDePasse) throws SQLException;

    List<Utilisateur> findGestionnaires() throws SQLException;

    boolean loginExiste(String login) throws SQLException;

    int insererGestionnaire(String login, String motDePasse) throws SQLException;

    boolean supprimerGestionnaire(int idUtilisateur) throws SQLException;
}
