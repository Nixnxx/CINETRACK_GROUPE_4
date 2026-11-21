package service;

import dao.ClientDAO;
import dao.ClientDAOImpl;
import model.ClientResume;

import java.util.List;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAOImpl();

    // Tous les clients avec leur nombre de billets valides et le total payé
    public List<ClientResume> getResumeClients() {
        return clientDAO.resumeClients();
    }
}
