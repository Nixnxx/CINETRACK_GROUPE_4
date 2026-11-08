package controleur;

import modele.LogEntree;
import service.LogService;

import java.sql.SQLException;
import java.util.List;

public class LogControleur {
    private final LogVue vue;
    private final LogService service = new LogService();

    public LogControleur(LogVue vue) {
        this.vue = vue;
    }

    // Les deux filtres peuvent être vides : on affiche alors tous les logs
    public void chargerLogs(String filtreTable, String filtreAction) {
        String action = filtreAction;
        if ("Toutes".equals(filtreAction)) {
            action = null;   // "Toutes" est un libellé de la liste déroulante
        }

        try {
            List<LogEntree> logs = service.getLogs(filtreTable, action);
            vue.afficherLogs(logs);
        } catch (SQLException e) {
            vue.afficherErreur("Erreur chargement des logs :\n" + e.getMessage());
        }
    }
}