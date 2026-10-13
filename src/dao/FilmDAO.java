package dao;

import modele.Film;
import exceptions.FilmIntrouvableException;
import connexion.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film ORDER BY titre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                films.add(extractFilmFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public Film findById(int id) {
        String sql = "SELECT * FROM film WHERE id_film = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFilmFromResultSet(rs);
                } else {
                    throw new FilmIntrouvableException(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur SQL lors de la recherche du film", e);
        }
    }

    public List<Film> findByGenre(String genre) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film WHERE LOWER(genre) = LOWER(?) ORDER BY titre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(extractFilmFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public boolean insert(Film film) {
        String sql = "INSERT INTO film (titre, genre, duree, note, synopsis) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuree());
            stmt.setDouble(4, film.getNote());
            stmt.setString(5, film.getSynopsis());
            int affected = stmt.executeUpdate();
            if (affected == 1) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) film.setIdFilm(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Film film) {
        String sql = "UPDATE film SET titre = ?, genre = ?, duree = ?, note = ?, synopsis = ? WHERE id_film = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuree());
            stmt.setDouble(4, film.getNote());
            stmt.setString(5, film.getSynopsis());
            stmt.setInt(6, film.getIdFilm());
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new FilmIntrouvableException(film.getIdFilm());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM film WHERE id_film = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) throw new FilmIntrouvableException(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Film extractFilmFromResultSet(ResultSet rs) throws SQLException {
        Film f = new Film();
        f.setIdFilm(rs.getInt("id_film"));
        f.setTitre(rs.getString("titre"));
        f.setGenre(rs.getString("genre"));
        f.setDuree(rs.getInt("duree"));
        f.setNote(rs.getDouble("note"));
        f.setSynopsis(rs.getString("synopsis"));
        return f;
    }
}