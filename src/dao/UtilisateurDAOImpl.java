package dao;

import database.DatabaseConnection;
import model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
  UtilisateurDAO – accès à la table utilisateur (SQL uniquement, aucune règle métier).
  Les mots de passe sont hachés par PostgreSQL (pgcrypto : crypt + gen_salt('bf', 10)).
  Les erreurs SQL sont remontées au contrôleur, qui décide du message à afficher.
 */
public class UtilisateurDAOImpl implements UtilisateurDAO {

    private static final String COLONNES = "id_utilisateur, login, role, date_creation";

    private Utilisateur mapper(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("date_creation");
        return new Utilisateur(
                rs.getInt("id_utilisateur"),
                rs.getString("login"),
                rs.getString("role"),
                ts != null ? ts.toLocalDateTime() : null);
    }

    /** Retourne l'utilisateur si login + mot de passe sont corrects, sinon null. */
    @Override
    public Utilisateur authentifier(String login, String motDePasse) throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM utilisateur "
                   + "WHERE LOWER(login) = LOWER(?) AND mot_de_passe = crypt(?, mot_de_passe)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, motDePasse);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapper(rs) : null;
            }
        }
    }

    @Override
    public List<Utilisateur> findGestionnaires() throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM utilisateur "
                   + "WHERE role = 'gestionnaire' ORDER BY LOWER(login)";
        List<Utilisateur> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public boolean loginExiste(String login) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE LOWER(login) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Crée un compte gestionnaire et retourne son id. */
    @Override
    public int insererGestionnaire(String login, String motDePasse) throws SQLException {
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, role) "
                   + "VALUES (?, crypt(?, gen_salt('bf', 10)), 'gestionnaire') "
                   + "RETURNING id_utilisateur";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, motDePasse);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /**
     * Supprime un gestionnaire. Le "AND role = 'gestionnaire'" garantit qu'un compte
     * admin ne peut jamais être supprimé par cette méthode.
     */
    @Override
    public boolean supprimerGestionnaire(int idUtilisateur) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ? AND role = 'gestionnaire'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            return stmt.executeUpdate() == 1;
        }
    }

    // =========================================================
    //  Anti force brute
    //  Les dates sont calculées par PostgreSQL (NOW()) pour éviter
    //  tout décalage d'heure entre l'application et la base.
    // =========================================================

    /** Minutes restantes avant la fin du blocage, 0 si le compte n'est pas bloqué. */
    @Override
    public int minutesRestantesBlocage(String login) throws SQLException {
        String sql = "SELECT CEIL(EXTRACT(EPOCH FROM (bloque_jusqua - NOW())) / 60.0) "
                   + "FROM utilisateur "
                   + "WHERE LOWER(login) = LOWER(?) AND bloque_jusqua > NOW()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Ajoute 1 au nombre d'échecs et retourne le nouveau total (0 si le login n'existe pas). */
    @Override
    public int enregistrerEchec(String login) throws SQLException {
        String sql = "UPDATE utilisateur SET tentatives_echec = tentatives_echec + 1 "
                   + "WHERE LOWER(login) = LOWER(?) RETURNING tentatives_echec";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Bloque le compte pendant "minutes" minutes ; le compteur d'échecs repart à 0. */
    @Override
    public void bloquer(String login, int minutes) throws SQLException {
        String sql = "UPDATE utilisateur "
                   + "SET bloque_jusqua = NOW() + (? * INTERVAL '1 minute'), tentatives_echec = 0 "
                   + "WHERE LOWER(login) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, minutes);
            stmt.setString(2, login);
            stmt.executeUpdate();
        }
    }

    /** Après une connexion réussie : compteur à 0 et plus de blocage. */
    @Override
    public void reinitialiserTentatives(String login) throws SQLException {
        String sql = "UPDATE utilisateur SET tentatives_echec = 0, bloque_jusqua = NULL "
                   + "WHERE LOWER(login) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.executeUpdate();
        }
    }
}
