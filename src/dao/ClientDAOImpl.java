package dao;

import database.DatabaseConnection;
import model.Client;
import model.ClientResume;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOImpl implements ClientDAO {

    // Cherche un client par son nom et sa date de naissance
    @Override
    public Client findByNomEtDate(String nom, LocalDate dateNaissance) {
        String sql = "SELECT * FROM client WHERE nom = ? AND date_naissance = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setDate(2, Date.valueOf(dateNaissance));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("id_client"),
                            rs.getString("nom"),
                            rs.getDate("date_naissance").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;   // client introuvable
    }

    // Ajoute un client et retourne l'id généré par la base
    @Override
    public int insert(Client client) {
        String sql = "INSERT INTO client (nom, date_naissance) VALUES (?, ?) RETURNING id_client";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, client.getNom());
            ps.setDate(2, Date.valueOf(client.getDateNaissance()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_client");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;   // échec
    }

    // Tous les clients avec leur nombre de billets valides et le total payé.
    // LEFT JOIN : un client sans billet valide apparaît quand même (0 billet, 0.00 payé).
    @Override
    public List<ClientResume> resumeClients() {
        List<ClientResume> clients = new ArrayList<>();
        String sql = "SELECT c.id_client, c.nom, c.date_naissance, "
                + "COUNT(b.id_billet) AS nb_billets, "
                + "COALESCE(SUM(b.prix), 0) AS total_paye "
                + "FROM client c "
                + "LEFT JOIN billet b ON b.id_client = c.id_client AND b.statut = 'valide' "
                + "GROUP BY c.id_client, c.nom, c.date_naissance "
                + "ORDER BY c.nom";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clients.add(new ClientResume(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getInt("nb_billets"),
                        rs.getDouble("total_paye")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
