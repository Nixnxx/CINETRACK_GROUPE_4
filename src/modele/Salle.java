package modele;

public class Salle {
    private int idSalle;
    private String nom;
    private int capacite;
    private String equipement;

    public Salle() {}

    public Salle(int idSalle, String nom, int capacite, String equipement) {
        this.idSalle = idSalle;
        this.nom = nom;
        this.capacite = capacite;
        this.equipement = equipement;
    }

    public Salle(String nom, int capacite, String equipement) {
        this(0, nom, capacite, equipement);
    }

    public int getIdSalle() { return idSalle; }
    public void setIdSalle(int idSalle) { this.idSalle = idSalle; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getEquipement() { return equipement; }
    public void setEquipement(String equipement) { this.equipement = equipement; }

    @Override
    public String toString() {
        return nom;
    }
}
