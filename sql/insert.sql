-- Salles
INSERT INTO salle (nom, capacite, equipement) VALUES
('Salle 1 - Étoile', 120, 'Dolby Atmos, 4K'),
('Salle 2 - Lumière', 80, 'Son 7.1, 3D'),
('Salle 3 - Panorama', 200, 'IMAX, Laser');

-- Films (8 films)
INSERT INTO film (titre, genre, duree, note, synopsis) VALUES
('Inception', 'Science-Fiction', 148, 8.8, 'Un voleur qui pénètre les rêves pour implanter une idée.'),
('Le Roi Lion', 'Animation', 88, 8.5, 'L''histoire du jeune Simba qui doit prendre sa place dans le cycle de la vie.'),
('Les Évadés', 'Drame', 142, 9.3, 'Deux prisonniers tissent un lien d''amitié sur plusieurs années.'),
('La La Land', 'Romance', 128, 8.0, 'Un pianiste de jazz et une actrice tombent amoureux à Los Angeles.'),
('Dune', 'Science-Fiction', 155, 8.1, 'Paul Atreides rejoint une guerre mortelle sur la planète désertique Arrakis.'),
('Intouchables', 'Comédie', 112, 8.6, 'Une amitié improbable entre un riche tétraplégique et son aide à domicile.'),
('Parasite', 'Thriller', 132, 8.6, 'Une famille s''infiltre chez les riches et les événements dégénèrent.'),
('Toy Story 4', 'Animation', 100, 7.8, 'Woody et Buzz font une nouvelle rencontre lors d''un voyage.'),
('Le Fabuleux Destin d''Amélie Poulain', 'Romance', 122, 8.3, 'Une jeune femme décide de changer la vie de ceux qui l''entourent.'),
('Mad Max: Fury Road', 'Action', 120, 8.7, 'Dans un monde post-apocalyptique, Furiosa et Max fuient un tyran.');

-- Séances (12 séances)
INSERT INTO seance (id_film, id_salle, date_seance, heure, places_dispo) VALUES
(1, 1, '2025-06-15', '14:00:00', 120),
(2, 2, '2025-06-15', '16:30:00', 80),
(3, 3, '2025-06-15', '20:00:00', 200),
(4, 1, '2025-06-16', '18:00:00', 110),
(5, 2, '2025-06-16', '21:00:00', 75),
(6, 3, '2025-06-17', '19:30:00', 190),
(7, 1, '2025-06-17', '22:00:00', 100),
(8, 2, '2025-06-18', '15:00:00', 80),
(1, 3, '2025-06-18', '20:30:00', 200),
(2, 1, '2025-06-19', '13:45:00', 115),
(3, 2, '2025-06-19', '17:15:00', 78),
(4, 3, '2025-06-20', '21:15:00', 195);