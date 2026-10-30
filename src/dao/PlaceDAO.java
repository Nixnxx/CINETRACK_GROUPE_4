package dao;
import java.sql.*;
import modele.Place;
import java.util.ArrayList;
import java.util.List;
import connexion.DatabaseConnection;

public class PlaceDAO {
    public List<Place> placeSalle(int idSalle) {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM place WHERE id_salle = ? ORDER BY rangee, numero";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSalle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    places.add(new Place(
                            rs.getInt("id_place"),
                            rs.getString("rangee"),
                            rs.getInt("numero"),
                            rs.getInt("id_salle")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }
    public List<Integer> placevendus(int idSeance) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_place FROM billet WHERE id_seance = ? AND statut = 'valide'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeance);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_place"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }




    }

