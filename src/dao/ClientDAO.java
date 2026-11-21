package dao;

import model.Client;
import model.ClientResume;

import java.time.LocalDate;
import java.util.List;

public interface ClientDAO {
    // Cherche un client par son nom et sa date de naissance (null s'il n'existe pas)
    Client findByNomEtDate(String nom, LocalDate dateNaissance);

    // Ajoute un client et retourne son id (-1 en cas d'échec)
    int insert(Client client);

    // Tous les clients avec leur nombre de billets valides et le total payé
    List<ClientResume> resumeClients();
}
