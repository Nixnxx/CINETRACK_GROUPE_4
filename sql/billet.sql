CREATE TYPE statut_billet AS ENUM ('valide', 'annule');

CREATE TABLE billet (
                        id_billet   SERIAL PRIMARY KEY,
                        nom_client  VARCHAR(100) NOT NULL,
                        id_seance   INTEGER NOT NULL REFERENCES seance(id_seance),
                        id_place    INTEGER NOT NULL REFERENCES place(id_place),
                        prix        NUMERIC(8,2) NOT NULL,
                        statut      statut_billet NOT NULL DEFAULT 'valide',
                        date_achat  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Une place ne peut avoir qu'un seul billet valide par séance
CREATE UNIQUE INDEX uq_billet_seance_place
    ON billet (id_seance, id_place)
    WHERE statut = 'valide';