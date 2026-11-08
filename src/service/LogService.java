package service;

import dao.LogDAO;
import dao.LogDAOImpl;
import modele.LogEntree;

import java.sql.SQLException;
import java.util.List;

public class LogService {

    private final LogDAO logDao = new LogDAOImpl();

    // Un filtre vide ou null signifie "pas de filtre"
    public List<LogEntree> getLogs(String filtreTable, String filtreAction) throws SQLException {
        return logDao.findLogs(nettoyer(filtreTable), nettoyer(filtreAction));
    }

    private String nettoyer(String filtre) {
        if (filtre == null || filtre.trim().isEmpty()) {
            return null;
        }
        return filtre.trim();
    }
}