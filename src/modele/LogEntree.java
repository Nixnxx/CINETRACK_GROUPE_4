package modele;

// Une ligne de la vue SQL v_logs_recents (affichée dans l'écran Logs)
public class LogEntree {

    private final int idLog;
    private final String tableCible;
    private final String action;
    private final String idCible;
    private final String detail;
    private final String effectuePar;
    private final String dateFormatee;

    public LogEntree(int idLog, String tableCible, String action, String idCible,
                     String detail, String effectuePar, String dateFormatee) {
        this.idLog = idLog;
        this.tableCible = tableCible;
        this.action = action;
        this.idCible = idCible;
        this.detail = detail;
        this.effectuePar = effectuePar;
        this.dateFormatee = dateFormatee;
    }

    public int getIdLog()            { return idLog; }
    public String getTableCible()    { return tableCible; }
    public String getAction()        { return action; }
    public String getIdCible()       { return idCible; }
    public String getDetail()        { return detail; }
    public String getEffectuePar()   { return effectuePar; }
    public String getDateFormatee()  { return dateFormatee; }
}
