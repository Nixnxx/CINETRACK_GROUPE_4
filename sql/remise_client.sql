-- =====================================================================
-- Tarification : remise de 20 % pour les clients de moins de 18 ans
-- (PostgreSQL) À exécuter UNE SEULE FOIS dans pgAdmin, base cinetrack4
-- =====================================================================

-- 1) Les anciens billets (de test) sont supprimés : ils n'ont pas de client
DELETE FROM billet;
ALTER SEQUENCE billet_id_billet_seq RESTART WITH 1;

-- 2) Nouvelle table client
CREATE TABLE client (
    id_client      SERIAL PRIMARY KEY,
    nom            VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    UNIQUE (nom, date_naissance)   -- même nom + même date = même client
);

-- 3) Table billet : on remplace nom_client par id_client, et on ajoute remise
ALTER TABLE billet DROP COLUMN nom_client;
ALTER TABLE billet ADD COLUMN id_client INTEGER NOT NULL REFERENCES client(id_client);
ALTER TABLE billet ADD COLUMN remise BOOLEAN NOT NULL DEFAULT FALSE;
