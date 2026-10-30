package controleur;

import modele.LogEntree;

import java.util.List;

// Ce que le contrôleur attend d'une vue "Logs"
public interface LogVue {

    void afficherLogs(List<LogEntree> logs);

    void afficherErreur(String message);
}
