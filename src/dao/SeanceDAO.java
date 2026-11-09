package dao;

import model.Seance;

import java.util.List;

public interface SeanceDAO {
    List<Seance> findAll();

    // MODIF 1 : séances à venir uniquement (pour la billetterie)
    List<Seance> findAVenir();

    // MODIF 2 : retrouver une séance (pour connaître sa salle et son prix)
    Seance findById(int idSeance);

    List<Seance> findByFilm(int idFilm);

    List<Seance> findByDate(String date);

    // MODIF 3 : le prix est maintenant inséré
    boolean insert(Seance seance);

    // MODIF 4 : message clair si la séance a déjà des billets
    boolean delete(int idSeance);
}
