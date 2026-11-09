CREATE table place (
    id_place SERIAL PRIMARY KEY ,
    rangee VARCHAR (5) NOT NULL ,
    numero INTEGER NOT NULL ,
    id_salle INTEGER NOT NULL REFERENCES salle(id_salle) ON DELETE CASCADE, UNIQUE (id_salle,rangee,numero)
);