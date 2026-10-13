
CREATE DATABASE cinetrack;
\c cinetrack;

-- Table film
CREATE TABLE IF NOT EXISTS film (
    id_film SERIAL PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duree INTEGER NOT NULL CHECK (duree > 0),
    note NUMERIC(3,1) NOT NULL CHECK (note >= 0 AND note <= 10),
    synopsis TEXT
);

-- Table salle
CREATE TABLE IF NOT EXISTS salle (
    id_salle SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    capacite INTEGER NOT NULL CHECK (capacite > 0),
    equipement TEXT
);

-- Table seance
CREATE TABLE IF NOT EXISTS seance (
    id_seance SERIAL PRIMARY KEY,
    id_film INTEGER NOT NULL REFERENCES film(id_film) ON DELETE CASCADE,
    id_salle INTEGER NOT NULL REFERENCES salle(id_salle) ON DELETE CASCADE,
    date_seance DATE NOT NULL,
    heure TIME NOT NULL,
    places_dispo INTEGER NOT NULL,
    UNIQUE(id_salle, date_seance, heure)
);