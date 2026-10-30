package dao;

import modele.LogEntree;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import connexion.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
  LogDAO – Permet d'insérer des entrées de log manuelles dans log_action. 
  Les triggers INSERT/UPDATE/DELETE.
  Auteur de l'action
  Logger des actions non couvertes par les triggers (ex: connexion, recherche).
 */
public class LogDAO {

    // Rôle courant
    private static String roleActuel = "systeme";

    /*
     Définit le rôle de l'utilisateur connecté ("gestionnaire" ou "admin").
     À appeler une fois après la connexion de l'utilisateur.
     */
    public static void setRoleActuel(String role) {
        roleActuel = (role != null && !role.isBlank()) ? role : "systeme";
    }

    public static String getRoleActuel() {
        return roleActuel;
    }

    public void insererLog(String tableCible, String action, Integer idCible, String detail) {
        String sql = "INSERT INTO log_action (table_cible, action, id_cible, detail, effectue_par) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableCible);
            stmt.setString(2, action);
            if (idCible != null) {
                stmt.setInt(3, idCible);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, detail);
            stmt.setString(5, roleActuel);

            stmt.executeUpdate();

        } catch (SQLException e) {
            // On ne bloque pas l'application si le log échoue
            System.err.println("[LogDAO] Erreur lors de l'insertion du log : " + e.getMessage());
        }
    }

    // ----------------
    //  Méthodes
    // ----------------

    public void logConnexion(String utilisateur) {
        insererLog("session", "CONNEXION",
                null,
                "Connexion de l'utilisateur : " + utilisateur + " (rôle=" + roleActuel + ")");
    }

    public void logAjoutFilm(int idFilm, String titre) {
        insererLog("film", "INSERT",
                idFilm,
                "Ajout du film \"" + titre + "\" par " + roleActuel);
    }

    public void logModificationFilm(int idFilm, String ancienTitre, String nouveauTitre) {
        insererLog("film", "UPDATE",
                idFilm,
                "Modification film id=" + idFilm
                        + " : \"" + ancienTitre + "\" → \"" + nouveauTitre
                        + "\" par " + roleActuel);
    }

    public void logSuppressionFilm(int idFilm, String titre) {
        insererLog("film", "DELETE",
                idFilm,
                "Suppression du film \"" + titre + "\" (id=" + idFilm + ") par " + roleActuel);
    }

    public void logAjoutSeance(int idSeance, int idFilm, String date) {
        insererLog("seance", "INSERT",
                idSeance,
                "Ajout séance id=" + idSeance
                        + " pour film id=" + idFilm
                        + " le " + date
                        + " par " + roleActuel);
    }

    public void logSuppressionSeance(int idSeance) {
        insererLog("seance", "DELETE",
                idSeance,
                "Suppression séance id=" + idSeance + " par " + roleActuel);
    }

    public void logAjoutUtilisateur(int idUtilisateur, String login) {
        insererLog("utilisateur", "INSERT",
                idUtilisateur,
                "Création du compte gestionnaire \"" + login + "\" (id=" + idUtilisateur + ") par " + roleActuel);
    }

    public void logSuppressionUtilisateur(int idUtilisateur, String login) {
        insererLog("utilisateur", "DELETE",
                idUtilisateur,
                "Suppression du compte gestionnaire \"" + login + "\" (id=" + idUtilisateur + ") par " + roleActuel);
    }

    public void logRecherche(String tableCible, String critere) {
        insererLog(tableCible, "RECHERCHE",
                null,
                "Recherche sur " + tableCible + " avec critère : \"" + critere + "\" par " + roleActuel);
    }

    // Récupération des logs avec filtres
    public List<LogEntree> findLogs(String filtreTable, String filtreAction) throws SQLException {
        List<LogEntree> logs = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id_log, table_cible, action, id_cible, detail, effectue_par, date_formatee " +
                        "FROM v_logs_recents WHERE 1=1"
        );

        if (filtreTable != null) {
            sql.append(" AND LOWER(table_cible) LIKE LOWER(?)");
        }

        if (filtreAction != null) {
            sql.append(" AND action = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int position = 1;

            if (filtreTable != null) {
                stmt.setString(position++, "%" + filtreTable + "%");
            }

            if (filtreAction != null) {
                stmt.setString(position, filtreAction);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object idCible = rs.getObject("id_cible");

                    String idCibleTexte = idCible != null
                            ? idCible.toString()
                            : "";

                    logs.add(new LogEntree(
                            rs.getInt("id_log"),
                            rs.getString("table_cible"),
                            rs.getString("action"),
                            idCibleTexte,
                            rs.getString("detail"),
                            rs.getString("effectue_par"),
                            rs.getString("date_formatee")
                    ));
                }
            }
        }

        return logs;
    }

}
