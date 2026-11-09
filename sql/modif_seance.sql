ALTER TABLE seance ADD COLUMN prix NUMERIC(8,2) NOT NULL DEFAULT 250.00;

UPDATE seance
SET date_seance = CURRENT_DATE + 1 + (date_seance - DATE '2025-06-15');

UPDATE seance s
SET places_dispo = sa.capacite
    FROM salle sa
WHERE sa.id_salle = s.id_salle;

INSERT INTO place (rangee, numero, id_salle)
SELECT chr(65 + (n - 1) / 10),
       ((n - 1) % 10) + 1,
    sa.id_salle
FROM salle sa
    CROSS JOIN LATERAL generate_series(1, sa.capacite) AS n;

SELECT id_salle, COUNT(*) FROM place GROUP BY id_salle ORDER BY id_salle;