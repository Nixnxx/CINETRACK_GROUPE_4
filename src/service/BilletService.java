package service;

import java.util.List;
import dao.BilletDAO;
import dao.PlaceDAO;
import dao.SeanceDAO;
import modele.Billet;
import modele.Place;
import modele.Seance;

public class BilletService {

    private final BilletDAO billetDAO = new BilletDAO();
    private final PlaceDAO placeDAO = new PlaceDAO();
    private final SeanceDAO seanceDAO = new SeanceDAO();

    // Les séances à venir (pour la liste de la billetterie)
    public List<Seance> seancesAVenir() {
        return seanceDAO.findAVenir();
    }

    // Toutes les places d'une salle (pour afficher la grille)
    public List<Place> placeSalle(int idSalle) {
        return placeDAO.placeSalle(idSalle);
    }

    // Les id des places déjà vendues pour une séance (pour les griser)
    public List<Integer> placeVendues(int idSeance) {
        return placeDAO.placevendus(idSeance);
    }

    // Retourne "OK" si l'achat a réussi, sinon le message d'erreur à afficher
    public String acheterBillet(String nomClient, int idSeance, int idPlace, double prix) {
        if (nomClient == null || nomClient.trim().isEmpty()) {
            return "Le nom du client est obligatoire.";
        }
        if (prix <= 0) {
            return "Le prix doit être positif.";
        }
        if (placeVendues(idSeance).contains(idPlace)) {
            return "Cette place est déjà vendue pour cette séance.";
        }

        Billet billet = new Billet(nomClient.trim(), idSeance, idPlace, prix);
        if (billetDAO.acheterBillet(billet)) {
            return "OK";
        }
        return "Achat impossible, réessayez.";
    }

    public boolean annulerBillet(int idBillet) {
        return billetDAO.annuler(idBillet);
    }

    public List<Billet> getBilletsSeance(int idSeance) {
        return billetDAO.billetsSeance(idSeance);
    }
}