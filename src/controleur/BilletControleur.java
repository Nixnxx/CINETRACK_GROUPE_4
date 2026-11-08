package controleur;

import modele.Place;
import service.BilletService;

import java.util.List;

public class BilletControleur {
    private final BilletVue vue;
    private final BilletService service = new BilletService();

    public BilletControleur(BilletVue vue) {
        this.vue = vue;
    }

    public void chargerSeances() {
        vue.afficherSeances(service.seancesAVenir());
    }

    public void chargerPlaces(int idSalle, int idSeance) {
        vue.afficherPlaces(service.placeSalle(idSalle), service.placeVendues(idSeance));
    }

    // Achète une place après l'autre, et s'arrête à la première erreur
    public void acheterBillets(String nomClient, int idSeance, double prix, List<Place> places) {
        int reussis = 0;
        String erreur = null;

        for (Place p : places) {
            String resultat = service.acheterBillet(nomClient, idSeance, p.getId_place(), prix);
            if (resultat.equals("OK")) {
                reussis++;
            } else {
                erreur = resultat;
                break;
            }
        }

        if (erreur == null) {
            vue.afficherSucces(reussis + " billet(s) acheté(s) !");
            vue.viderFormulaire();
        } else {
            vue.afficherErreur(reussis + " billet(s) acheté(s), puis : " + erreur);
        }
    }
}