package dao;

import modele.Salle;
import connexion.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> findAll() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle ORDER BY nom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                salles.add(new Salle(
                    rs.getInt("id_salle"),
                    rs.getString("nom"),
                    rs.getInt("capacite"),
                    rs.getString("equipement")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    public Salle findById(int id) {
        String sql = "SELECT * FROM salle WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Salle(
                        rs.getInt("id_salle"),
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        rs.getString("equipement")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}