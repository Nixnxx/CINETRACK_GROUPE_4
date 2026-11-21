package model;

import java.time.LocalDate;

/*
  ClientResume : une ligne du tableau « Clients » (écran réservé à l'admin).
  Ce n'est pas une table de la base : les valeurs viennent d'une requête
  qui regroupe les billets valides de chaque client.
 */
public class ClientResume {

    private int idClient;
    private String nom;
    private LocalDate dateNaissance;
    private int nbBillets;      // nombre de billets valides achetés
    private double totalPaye;   // somme des prix payés (remise déjà déduite)

    public ClientResume(int idClient, String nom, LocalDate dateNaissance, int nbBillets, double totalPaye) {
        this.idClient = idClient;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.nbBillets = nbBillets;
        this.totalPaye = totalPaye;
    }

    public int getIdClient() {
        return idClient;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public int getNbBillets() {
        return nbBillets;
    }

    public double getTotalPaye() {
        return totalPaye;
    }
}
