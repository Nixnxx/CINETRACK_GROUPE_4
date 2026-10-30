package modele;

public class Place {
    private int id_place;
    private String rangee ;
    private int numero ;
    private int id_salle ;


    public Place ( String rangee , int numero , int id_salle ) {
        this.rangee =rangee ;
        this.numero = numero ;
        this.id_salle = id_salle ;
    }
    public Place (int id_place, String rangee, int numero, int id_salle) {
        this.id_place = id_place;
        this.rangee = rangee;
        this.numero = numero;
        this.id_salle = id_salle;
    }

    public int getId_place() {
        return id_place;

    }
    public String getRangee() {
        return rangee;
    }
    public int getNumero() {
        return numero;
    }
    public int getId_salle() {
        return id_salle;
    }
}
