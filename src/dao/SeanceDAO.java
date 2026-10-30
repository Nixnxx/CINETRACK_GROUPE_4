package dao;

import modele.Seance;
import exceptions.SalleIndisponibleException;
import connexion.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    public List<Seance> findAll() {
        List<Seance> seances = new ArrayList<>();
        String sql = "SELECT s.*, f.titre as titre_film, sa.nom as nom_salle " +
                "FROM seance s " +
                "JOIN film f ON s.id_film = f.id_film " +
                "JOIN salle sa ON s.id_salle = sa.id_salle " +
                "ORDER BY s.date_seance, s.heure";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                seances.add(extractSeanceWithJoin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // MODIF 1 : séances à venir uniquement (pour la billetterie)
    public List<Seance> findAVenir() {
        List<Seance> seances = new ArrayList<>();
        String sql = "SELECT s.*, f.titre as titre_film, sa.nom as nom_salle " +
                "FROM seance s " +
                "JOIN film f ON s.id_film = f.id_film " +
                "JOIN salle sa ON s.id_salle = sa.id_salle " +
                "WHERE s.date_seance > CURRENT_DATE " +
                "   OR (s.date_seance = CURRENT_DATE AND s.heure > CURRENT_TIME) " +
                "ORDER BY s.date_seance, s.heure";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                seances.add(extractSeanceWithJoin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // MODIF 2 : retrouver une séance (pour connaître sa salle et son prix)
    public Seance findById(int idSeance) {
        String sql = "SELECT s.*, f.titre as titre_film, sa.nom as nom_salle " +
                "FROM seance s " +
                "JOIN film f ON s.id_film = f.id_film " +
                "JOIN salle sa ON s.id_salle = sa.id_salle " +
                "WHERE s.id_seance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSeance);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSeanceWithJoin(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;   // aucune séance avec cet id
    }

    public List<Seance> findByFilm(int idFilm) {
        List<Seance> seances = new ArrayList<>();
        String sql = "SELECT s.*, f.titre as titre_film, sa.nom as nom_salle " +
                "FROM seance s " +
                "JOIN film f ON s.id_film = f.id_film " +
                "JOIN salle sa ON s.id_salle = sa.id_salle " +
                "WHERE s.id_film = ? ORDER BY s.date_seance, s.heure";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFilm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seances.add(extractSeanceWithJoin(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    public List<Seance> findByDate(String date) {
        List<Seance> seances = new ArrayList<>();
        String sql = "SELECT s.*, f.titre as titre_film, sa.nom as nom_salle " +
                "FROM seance s " +
                "JOIN film f ON s.id_film = f.id_film " +
                "JOIN salle sa ON s.id_salle = sa.id_salle " +
                "WHERE s.date_seance = ? ORDER BY s.heure";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seances.add(extractSeanceWithJoin(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // MODIF 3 : le prix est maintenant inséré
    public boolean insert(Seance seance) {
        String sql = "INSERT INTO seance (id_film, id_salle, date_seance, heure, places_dispo, prix) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, seance.getIdFilm());
            stmt.setInt(2, seance.getIdSalle());
            stmt.setDate(3, Date.valueOf(seance.getDateSeance()));
            stmt.setTime(4, Time.valueOf(seance.getHeure()));
            stmt.setInt(5, seance.getPlacesDispo());
            stmt.setDouble(6, seance.getPrix());
            int affected = stmt.executeUpdate();
            if (affected == 1) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) seance.setIdSeance(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new SalleIndisponibleException(seance.getIdSalle(),
                        seance.getDateSeance() + " " + seance.getHeure());
            }
            e.printStackTrace();
            return false;
        }
    }

    // MODIF 4 : message clair si la séance a déjà des billets
    public boolean delete(int idSeance) {
        String sql = "DELETE FROM seance WHERE id_seance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSeance);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {   // clé étrangère : des billets existent
                throw new IllegalStateException(
                        "Impossible de supprimer : des billets existent pour cette séance.");
            }
            e.printStackTrace();
            return false;
        }
    }

    private Seance extractSeanceWithJoin(ResultSet rs) throws SQLException {
        Seance s = new Seance();
        s.setIdSeance(rs.getInt("id_seance"));
        s.setIdFilm(rs.getInt("id_film"));
        s.setIdSalle(rs.getInt("id_salle"));
        s.setDateSeance(rs.getDate("date_seance").toLocalDate());
        s.setHeure(rs.getTime("heure").toLocalTime());
        s.setPlacesDispo(rs.getInt("places_dispo"));
        s.setPrix(rs.getDouble("prix"));   // MODIF 3 : lecture du prix
        s.setTitreFilm(rs.getString("titre_film"));
        s.setNomSalle(rs.getString("nom_salle"));
        return s;
    }
}