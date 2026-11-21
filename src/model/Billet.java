package model;

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
    private int id_client;
    private int id_seance;
    private int id_place;
    private double prix;          // prix final, remise déjà déduite
    private boolean remise;       // true si le client a moins de 18 ans
    private Date date_achat;
    private Statut statut;

    //CONSTRUCTEUR POUR L'INSERTION
    public Billet(int id_client, int id_seance, int id_place, double prix, boolean remise) {
        this.id_client = id_client;
        this.id_seance = id_seance;
        this.id_place = id_place;
        this.prix = prix;
        this.remise = remise;
        this.statut = Statut.VALIDE;
    }
    //CONSTRUCTEUR UTILE pour SELECT
    public Billet(int id_billet, int id_client, int id_seance, int id_place, double prix, boolean remise, Date date_achat, Statut statut) {
        this.id_billet = id_billet;
        this.id_client = id_client;
        this.id_seance = id_seance;
        this.id_place = id_place;
        this.prix = prix;
        this.remise = remise;
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
    public int getId_client() {
        return id_client;
    }
    public double getPrix() {
        return prix;
    }
    public boolean isRemise() {
        return remise;
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
