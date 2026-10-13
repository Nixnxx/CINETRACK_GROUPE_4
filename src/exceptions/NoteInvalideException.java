package exceptions;

public class NoteInvalideException extends RuntimeException {
    private double valeurInvalide;

    public NoteInvalideException(double valeur) {
        super("Note invalide : " + valeur + " (doit être comprise entre 0 et 10)");
        this.valeurInvalide = valeur;
    }

    public double getValeurInvalide() { return valeurInvalide; }
}