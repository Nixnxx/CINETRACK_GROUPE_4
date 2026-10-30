package controleur;

import dao.LogDAO;
import modele.LogEntree;

import java.sql.SQLException;
import java.util.List;

public class LogControleur {
    private LogVue vue;
    private LogDAO dao;

    public LogControleur(LogVue vue) {
        this.vue = vue;
        this.dao = new LogDAO();
    }

    // Les deux filtres peuvent être vides : on affiche alors tous les logs
    public void chargerLogs(String filtreTable, String filtreAction) {
        String table = null;
        if (filtreTable != null && !filtreTable.trim().isEmpty()) {
            table = filtreTable.trim();
        }

        String action = null;
        if (filtreAction != null && !"Toutes".equals(filtreAction)) {
            action = filtreAction;
        }

        try {
            List<LogEntree> logs = dao.findLogs(table, action);
            vue.afficherLogs(logs);
        } catch (SQLException e) {
            vue.afficherErreur("Erreur chargement des logs :\n" + e.getMessage());
        }
    }
}
