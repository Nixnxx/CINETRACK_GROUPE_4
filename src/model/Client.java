package model;

import java.time.LocalDate;

public class Client {

    private int idClient;
    private String nom;
    private LocalDate dateNaissance;

    // Constructeur pour l'insertion (l'id est généré par la base)
    public Client(String nom, LocalDate dateNaissance) {
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    // Constructeur pour le SELECT
    public Client(int idClient, String nom, LocalDate dateNaissance) {
        this.idClient = idClient;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
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
}
