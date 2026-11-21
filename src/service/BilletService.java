package service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import dao.*;
import model.Billet;
import model.Client;
import model.Place;
import model.Seance;

public class BilletService {

    // Règle de tarification : moins de 18 ans => 20 % de remise
    private static final int AGE_REMISE = 18;
    private static final double TAUX_REMISE = 0.20;

    private final BilletDAO billetDAO = new BilletDAOImpl();
    private final ClientDAO clientDAO = new ClientDAOImpl();
    private final PlaceDAO placeDAO = new PlaceDAOImpl();
    private final SeanceDAO seanceDAO = new SeanceDAOImpl();

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

    // =========================================================
    //  Règle métier : la remise
    // =========================================================

    // Âge du client aujourd'hui (jour de l'achat)
    public int calculerAge(LocalDate dateNaissance) {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    // true si le client a moins de 18 ans
    public boolean aDroitRemise(LocalDate dateNaissance) {
        return calculerAge(dateNaissance) < AGE_REMISE;
    }

    // Prix à payer : prix de la séance, moins 20 % si le client a droit à la remise
    public double prixFinal(double prixSeance, LocalDate dateNaissance) {
        if (aDroitRemise(dateNaissance)) {
            double prix = prixSeance * (1 - TAUX_REMISE);
            return Math.round(prix * 100.0) / 100.0;   // arrondi à 2 décimales
        }
        return prixSeance;
    }

    // =========================================================
    //  Achat
    // =========================================================

    // Retourne "OK" si l'achat a réussi, sinon le message d'erreur à afficher
    // prixSeance = prix de base de la séance (la remise est calculée ici)
    public String acheterBillet(String nomClient, LocalDate dateNaissance,
                                int idSeance, int idPlace, double prixSeance) {
        if (nomClient == null || nomClient.trim().isEmpty()) {
            return "Le nom du client est obligatoire.";
        }
        if (dateNaissance == null) {
            return "La date de naissance est obligatoire.";
        }
        if (dateNaissance.isAfter(LocalDate.now())) {
            return "La date de naissance est invalide.";
        }
        if (prixSeance <= 0) {
            return "Le prix doit être positif.";
        }
        if (placeVendues(idSeance).contains(idPlace)) {
            return "Cette place est déjà vendue pour cette séance.";
        }

        // 1) Le client : on le retrouve s'il existe déjà, sinon on le crée
        String nom = nomClient.trim();
        Client client = clientDAO.findByNomEtDate(nom, dateNaissance);
        int idClient;
        if (client != null) {
            idClient = client.getIdClient();
        } else {
            idClient = clientDAO.insert(new Client(nom, dateNaissance));
            if (idClient == -1) {
                return "Impossible d'enregistrer le client.";
            }
        }

        // 2) La remise et le prix final
        boolean remise = aDroitRemise(dateNaissance);
        double prix = prixFinal(prixSeance, dateNaissance);

        // 3) Le billet
        Billet billet = new Billet(idClient, idSeance, idPlace, prix, remise);
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
