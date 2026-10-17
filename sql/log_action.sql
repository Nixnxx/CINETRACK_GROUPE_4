-- ============================================================
--  CineTrack – Système de log
--  À exécuter dans la base de données cinetrack (PostgreSQL)
-- ============================================================

-- Table de log
CREATE TABLE IF NOT EXISTS log_action (
    id_log     SERIAL PRIMARY KEY,
    table_cible VARCHAR(50)  NOT NULL,          -- 'film' ou 'seance'
    action      VARCHAR(10)  NOT NULL,           -- 'INSERT', 'UPDATE', 'DELETE'
    id_cible    INTEGER,                         -- id de l'enregistrement concerné
    detail      TEXT,                            -- description lisible
    effectue_par VARCHAR(50) DEFAULT 'systeme',  -- 'gestionnaire' ou 'admin' (ou trigger)
    date_action TIMESTAMP    DEFAULT NOW()
);

-- ============================================================
--  FONCTION générique appelée par les triggers
-- ============================================================
CREATE OR REPLACE FUNCTION fn_log_action()
RETURNS TRIGGER AS $$
DECLARE
    v_id      INTEGER;
    v_detail  TEXT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_id := OLD.id_film;  -- sera surchargé si table seance
        -- Récupérer le bon id selon la table
        IF TG_TABLE_NAME = 'seance' THEN
            v_id := OLD.id_seance;
            v_detail := 'Suppression séance id=' || OLD.id_seance
                        || ' (film=' || OLD.id_film
                        || ', salle=' || OLD.id_salle
                        || ', date=' || OLD.date_seance || ')';
        ELSE
            v_id := OLD.id_film;
            v_detail := 'Suppression film id=' || OLD.id_film
                        || ' titre=' || OLD.titre;
        END IF;

        INSERT INTO log_action (table_cible, action, id_cible, detail)
        VALUES (TG_TABLE_NAME, 'DELETE', v_id, v_detail);
        RETURN OLD;

    ELSIF TG_OP = 'INSERT' THEN
        IF TG_TABLE_NAME = 'seance' THEN
            v_id := NEW.id_seance;
            v_detail := 'Ajout séance id=' || NEW.id_seance
                        || ' (film=' || NEW.id_film
                        || ', salle=' || NEW.id_salle
                        || ', date=' || NEW.date_seance
                        || ', heure=' || NEW.heure || ')';
        ELSE
            v_id := NEW.id_film;
            v_detail := 'Ajout film id=' || NEW.id_film
                        || ' titre=' || NEW.titre
                        || ' genre=' || NEW.genre;
        END IF;

        INSERT INTO log_action (table_cible, action, id_cible, detail)
        VALUES (TG_TABLE_NAME, 'INSERT', v_id, v_detail);
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        IF TG_TABLE_NAME = 'seance' THEN
            v_id := NEW.id_seance;
            v_detail := 'Modification séance id=' || NEW.id_seance
                        || ' (date: ' || OLD.date_seance || ' → ' || NEW.date_seance
                        || ', heure: ' || OLD.heure || ' → ' || NEW.heure || ')';
        ELSE
            v_id := NEW.id_film;
            v_detail := 'Modification film id=' || NEW.id_film
                        || ' titre: ' || OLD.titre || ' → ' || NEW.titre
                        || ', note: ' || OLD.note || ' → ' || NEW.note;
        END IF;

        INSERT INTO log_action (table_cible, action, id_cible, detail)
        VALUES (TG_TABLE_NAME, 'UPDATE', v_id, v_detail);
        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
--  TRIGGERS sur la table film
-- ============================================================
DROP TRIGGER IF EXISTS trg_log_film ON film;

CREATE TRIGGER trg_log_film
AFTER INSERT OR UPDATE OR DELETE ON film
FOR EACH ROW EXECUTE FUNCTION fn_log_action();

-- ============================================================
--  TRIGGERS sur la table seance
-- ============================================================
DROP TRIGGER IF EXISTS trg_log_seance ON seance;

CREATE TRIGGER trg_log_seance
AFTER INSERT OR UPDATE OR DELETE ON seance
FOR EACH ROW EXECUTE FUNCTION fn_log_action();

-- ============================================================
--  Vue pratique pour consulter les logs facilement
-- ============================================================
CREATE OR REPLACE VIEW v_logs_recents AS
SELECT
    id_log,
    table_cible,
    action,
    id_cible,
    detail,
    effectue_par,
    TO_CHAR(date_action, 'DD/MM/YYYY HH24:MI:SS') AS date_formatee
FROM log_action
ORDER BY date_action DESC;
