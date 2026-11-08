package dao;

import modele.Film;

import java.util.List;

public interface FilmDAO {
    List<Film> findAll();

    Film findById(int id);

    List<Film> findByGenre(String genre);

    boolean insert(Film film);

    boolean update(Film film);

    boolean delete(int id);
}
