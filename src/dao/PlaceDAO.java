package dao;

import modele.Place;

import java.util.List;

public interface PlaceDAO {
    List<Place> placeSalle(int idSalle);

    List<Integer> placevendus(int idSeance);
}
