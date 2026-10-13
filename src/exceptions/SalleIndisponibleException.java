package exceptions;

public class SalleIndisponibleException extends RuntimeException {
    private int idSalle;
    private String dateHeure;

    public SalleIndisponibleException(int idSalle, String dateHeure) {
        super("La salle " + idSalle + " est déjà réservée à la date/heure : " + dateHeure);
        this.idSalle = idSalle;
        this.dateHeure = dateHeure;
    }

    public int getIdSalle() { return idSalle; }
    public String getDateHeure() { return dateHeure; }
}