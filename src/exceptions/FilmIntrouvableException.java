package exceptions;

public class FilmIntrouvableException extends RuntimeException {
    private int idFilm;

    public FilmIntrouvableException(int idFilm) {
        super("Aucun film trouvé avec l'id : " + idFilm);
        this.idFilm = idFilm;
    }

    public int getIdFilm() { return idFilm; }
}