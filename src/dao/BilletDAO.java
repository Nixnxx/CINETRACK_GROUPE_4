package dao;

import modele.Billet;

import java.util.List;

public interface BilletDAO {
    // Acheter un billet
    boolean acheterBillet(Billet billet);

    // Lire les billets d'une séance
    List<Billet> billetsSeance(int idSeance);

    boolean annuler(int idBillet);
}
