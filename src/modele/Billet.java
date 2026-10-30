package modele;

import java.util.Date;

public class Billet {

    public enum Statut {
        VALIDE("valide"),
        ANNULE("annule");

        private final String valeurBdd;

        Statut(String valeurBdd) {
            this.valeurBdd = valeurBdd;
        }

        public String getValeurBdd() {
            return valeurBdd;
        }

        public static Statut fromBdd(String valeur) {
            for (Statut s : values()) {
                if (s.valeurBdd.equals(valeur)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Statut inconnu : " + valeur);
        }
    }

    private int id_billet;
    private String nom_client;
    private int id_seance;
    private int id_place;
    private double prix;
    private Date date_achat;
    private Statut statut;

    //CONSTRUCTEUR POUR L'INSERTION
    public Billet(String nom_client, int id_seance, int id_place, double prix) {
        this.nom_client = nom_client;
        this.id_seance = id_seance;
        this.id_place = id_place;
        this.prix = prix;
        this.statut = Statut.VALIDE;
    }
    //CONSTRUCTEUR UTILE pour SELECT
    public Billet(int id_billet ,String nom_client,int id_seance,int id_place,double prix ,Date date_achat ,Statut statut) {
        this.id_billet = id_billet;
        this.nom_client = nom_client;
        this.id_seance = id_seance;
        this.id_place = id_place;
        this.prix = prix;
        this.date_achat = date_achat;
        this.statut = statut;
    }
    public int getId_billet() {
        return id_billet;
    }
    public int getId_seance() {
        return id_seance;
    }
    public int getId_place() {
        return id_place;
    }
    public String getNom_client() {
        return nom_client;
    }
    public double getPrix() {
        return prix;
    }
    public Date getDate_achat() {
        return date_achat;
    }
    public Statut getStatut() {
        return statut;
    }
    public void setPrix(double prix) {
        if (prix > 0) {
            this.prix = prix;
        }
        else {
            throw new IllegalArgumentException("le prix est invalide : " + prix);
        }
    }
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
}