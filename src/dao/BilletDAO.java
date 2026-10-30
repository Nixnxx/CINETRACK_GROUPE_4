package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connexion.DatabaseConnection;
import modele.Billet;

public class BilletDAO {

    // Acheter un billet
    public boolean acheterBillet(Billet billet) {
        String sql = "INSERT INTO billet (nom_client, id_seance, id_place, prix) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, billet.getNom_client());
            ps.setInt(2, billet.getId_seance());
            ps.setInt(3, billet.getId_place());
            ps.setDouble(4, billet.getPrix());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;   // échec, par exemple place déjà vendue
        }
    }

    // Lire les billets d'une séance
    public List<Billet> billetsSeance(int idSeance) {
        List<Billet> billets = new ArrayList<>();
        String sql = "SELECT * FROM billet WHERE id_seance = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeance);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    billets.add(new Billet(
                            rs.getInt("id_billet"),
                            rs.getString("nom_client"),
                            rs.getInt("id_seance"),
                            rs.getInt("id_place"),
                            rs.getDouble("prix"),
                            rs.getTimestamp("date_achat"),
                            Billet.Statut.fromBdd(rs.getString("statut"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billets;
    }
    public boolean annuler(int idBillet) {
        String sql = "UPDATE billet SET statut = 'annule' "
                + "WHERE id_billet = ? AND statut = 'valide'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idBillet);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
