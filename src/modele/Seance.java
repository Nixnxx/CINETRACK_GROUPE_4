package modele;

import java.time.LocalDate;
import java.time.LocalTime;

public class Seance {
    private int idSeance;
    private int idFilm;
    private int idSalle;
    private LocalDate dateSeance;
    private LocalTime heure;
    private int placesDispo;
    private double prix;
    
    // Champs pour affichage (jointure)
    private String titreFilm;
    private String nomSalle;

    // Constructeurs
    public Seance() {}

    public Seance(int idSeance, int idFilm, int idSalle, LocalDate dateSeance, LocalTime heure, int placesDispo,double prix) {
        this.idSeance = idSeance;
        this.idFilm = idFilm;
        this.idSalle = idSalle;
        this.dateSeance = dateSeance;
        this.heure = heure;
        this.placesDispo = placesDispo;
        this.prix = prix;
    }

    public Seance(int idFilm, int idSalle, LocalDate dateSeance, LocalTime heure, int placesDispo,double prix) {
        this(0, idFilm, idSalle, dateSeance, heure, placesDispo,prix);
    }

    // Getters / Setters
    public int getIdSeance() { return idSeance; }
    public void setIdSeance(int idSeance) { this.idSeance = idSeance; }
    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }
    public int getIdSalle() { return idSalle; }
    public void setIdSalle(int idSalle) { this.idSalle = idSalle; }
    public LocalDate getDateSeance() { return dateSeance; }
    public void setDateSeance(LocalDate dateSeance) { this.dateSeance = dateSeance; }
    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }
    public int getPlacesDispo() { return placesDispo; }
    public void setPlacesDispo(int placesDispo) { this.placesDispo = placesDispo; }
    
    public String getTitreFilm() { return titreFilm; }
    public void setTitreFilm(String titreFilm) { this.titreFilm = titreFilm; }
    public String getNomSalle() { return nomSalle; }
    public void setNomSalle(String nomSalle) { this.nomSalle = nomSalle; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    @Override
    public String toString() {
        return "Seance #" + idSeance + " - " + dateSeance + " " + heure;
    }

    public String getLibelle() {
        return heure + " - " + titreFilm + " - " + nomSalle + " - " + prix;
    }
}
