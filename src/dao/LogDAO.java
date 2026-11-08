package dao;

import modele.LogEntree;

import java.sql.SQLException;
import java.util.List;

public interface LogDAO {
    /*
         Définit le rôle de l'utilisateur connecté ("gestionnaire" ou "admin").
         À appeler une fois après la connexion de l'utilisateur.
         */
    static void setRoleActuel(String role) {
        LogDAOImpl.roleActuel = (role != null && !role.isBlank()) ? role : "systeme";
    }

    static String getRoleActuel() {
        return LogDAOImpl.roleActuel;
    }

    void insererLog(String tableCible, String action, Integer idCible, String detail);

    void logConnexion(String utilisateur);

    void logAjoutFilm(int idFilm, String titre);

    void logModificationFilm(int idFilm, String ancienTitre, String nouveauTitre);

    void logSuppressionFilm(int idFilm, String titre);

    void logAjoutSeance(int idSeance, int idFilm, String date);

    void logSuppressionSeance(int idSeance);

    void logAjoutUtilisateur(int idUtilisateur, String login);

    void logSuppressionUtilisateur(int idUtilisateur, String login);

    void logRecherche(String tableCible, String critere);

    // Récupération des logs avec filtres
    List<LogEntree> findLogs(String filtreTable, String filtreAction) throws SQLException;
}
