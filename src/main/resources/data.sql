-- Crear las tablas
CREATE TABLE IF NOT EXISTS player (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rating DOUBLE DEFAULT 1500,
    rd DOUBLE DEFAULT 350,
    volatility DOUBLE DEFAULT 0.06,
    last_match_date TIMESTAMP
);

CREATE TABLE match (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_a_id BIGINT NOT NULL,
    player_b_id BIGINT NOT NULL,
    result DOUBLE NOT NULL,
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (player_a_id) REFERENCES player(id),
    FOREIGN KEY (player_b_id) REFERENCES player(id)
);

-- Insertar datos en la tabla player
--INSERT INTO player (name) VALUES ('Jugador 1');
--INSERT INTO player (name) VALUES ('Jugador 2');
--INSERT INTO player (name) VALUES ('Jugador 3');

-- Insertar datos en la tabla match (asegúrate de que los IDs sean correctos)
--INSERT INTO match (player_a_id, player_b_id, result, date) VALUES (1, 2, 1.0, CURRENT_TIMESTAMP);
--INSERT INTO match (player_a_id, player_b_id, result, date) VALUES (2, 3, 0.5, CURRENT_TIMESTAMP);
--INSERT INTO match (player_a_id, player_b_id, result, date) VALUES (3, 1, 0.0, CURRENT_TIMESTAMP);