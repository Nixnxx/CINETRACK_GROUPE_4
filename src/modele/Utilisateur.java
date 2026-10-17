package modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
  Utilisateur – compte de connexion (admin ou gestionnaire).
  Ne contient volontairement PAS le mot de passe (ni son hash).
 */
public class Utilisateur {

    public static final String ROLE_ADMIN        = "admin";
    public static final String ROLE_GESTIONNAIRE = "gestionnaire";

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int           idUtilisateur;
    private final String        login;
    private final String        role;
    private final LocalDateTime dateCreation;

    public Utilisateur(int idUtilisateur, String login, String role, LocalDateTime dateCreation) {
        this.idUtilisateur = idUtilisateur;
        this.login         = login;
        this.role          = role;
        this.dateCreation  = dateCreation;
    }

    public int           getIdUtilisateur() { return idUtilisateur; }
    public String        getLogin()         { return login; }
    public String        getRole()          { return role; }
    public LocalDateTime getDateCreation()  { return dateCreation; }

    public boolean estAdmin() { return ROLE_ADMIN.equals(role); }

    public String getDateCreationFormatee() {
        return dateCreation != null ? dateCreation.format(FORMAT_DATE) : "";
    }

    @Override
    public String toString() { return login + " (" + role + ")"; }
}
