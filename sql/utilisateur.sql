-- Extension qui fournit crypt() / gen_salt() (hachage bcrypt)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur SERIAL       PRIMARY KEY,
    login          VARCHAR(50)  NOT NULL,
    mot_de_passe   VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL CHECK (role IN ('admin', 'gestionnaire')),
    date_creation  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_utilisateur_login
    ON utilisateur (LOWER(login));

INSERT INTO utilisateur (login, mot_de_passe, role)
SELECT 'admin', crypt('admin', gen_salt('bf', 10)), 'admin'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE LOWER(login) = 'admin');

INSERT INTO utilisateur (login, mot_de_passe, role)
SELECT 'gestionnaire', crypt('gestionnaire', gen_salt('bf', 10)), 'gestionnaire'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE LOWER(login) = 'gestionnaire');

CREATE OR REPLACE FUNCTION fn_log_utilisateur()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO log_action (table_cible, action, id_cible, detail)
        VALUES ('utilisateur', 'INSERT', NEW.id_utilisateur,
                'Création du compte "' || NEW.login || '" (rôle=' || NEW.role || ')');
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO log_action (table_cible, action, id_cible, detail)
        VALUES ('utilisateur', 'DELETE', OLD.id_utilisateur,
                'Suppression du compte "' || OLD.login || '" (rôle=' || OLD.role || ')');
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_log_utilisateur ON utilisateur;

CREATE TRIGGER trg_log_utilisateur
AFTER INSERT OR DELETE ON utilisateur
FOR EACH ROW EXECUTE FUNCTION fn_log_utilisateur();
