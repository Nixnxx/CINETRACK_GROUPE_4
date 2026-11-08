package dao;

import modele.Salle;

import java.util.List;

public interface SalleDAO {
    List<Salle> findAll();

    Salle findById(int id);
}
