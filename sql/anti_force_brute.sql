-- =====================================================================
-- Anti force brute : après 5 mots de passe faux, le compte est bloqué
-- (PostgreSQL) À exécuter UNE SEULE FOIS dans pgAdmin, base cinetrack4
-- =====================================================================

-- tentatives_echec : nombre de mots de passe faux de suite
-- bloque_jusqua    : date de fin du blocage (NULL = compte non bloqué)
ALTER TABLE utilisateur ADD COLUMN tentatives_echec INTEGER NOT NULL DEFAULT 0;
ALTER TABLE utilisateur ADD COLUMN bloque_jusqua TIMESTAMP;

-- Pour débloquer un compte à la main (test ou urgence) :
-- UPDATE utilisateur SET tentatives_echec = 0, bloque_jusqua = NULL WHERE login = 'admin';
