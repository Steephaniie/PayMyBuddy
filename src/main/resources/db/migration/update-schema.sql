DROP TABLE users_connections;
DROP TABLE transactions;
DROP TABLE users;

-- Création de la table des transactions qui contient les détails des transferts effectués entre les utilisateurs
CREATE TABLE transactions
(
    id            BIGINT NOT NULL AUTO_INCREMENT,              -- Identifiant unique pour chaque transaction
    sender_id     BIGINT NOT NULL,              -- Identifiant de l'utilisateur envoyant l'argent
    receiver_id   BIGINT NOT NULL,              -- Identifiant de l'utilisateur recevant l'argent
    amount        DECIMAL(7,2) NOT NULL,                     -- Montant de la transaction
    `description` VARCHAR(255) NULL,            -- Description facultative de la transaction
    CONSTRAINT pk_transactions PRIMARY KEY (id) -- Définition de la clé primaire sur le champ 'id'
);

-- Création de la table des utilisateurs qui contient les informations d'identité et de connexion
CREATE TABLE users
(
    id       BIGINT NOT NULL AUTO_INCREMENT,            -- Identifiant unique pour chaque utilisateur
    username VARCHAR(255) NULL,          -- Nom d'utilisateur (facultatif)
    password VARCHAR(255) NULL,          -- Mot de passe de l'utilisateur (facultatif)
    email    VARCHAR(255) NULL,          -- Adresse email de l'utilisateur (facultatif)
    solde    DECIMAL(7,2) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id) -- Définition de la clé primaire sur le champ 'id'
);

-- Création de la table pour gérer les connexions ou "amis" entre utilisateurs
CREATE TABLE users_connections
(
    user_id        BIGINT NOT NULL, -- Identifiant de l'utilisateur
    connections_id BIGINT NOT NULL  -- Identifiant de son ami ou connexion
);

-- Ajout d'une contrainte pour assurer l'unicité des connexions dans la table 'users_connections'
ALTER TABLE users_connections
    ADD CONSTRAINT uc_users_connections_connections UNIQUE (connections_id,user_id);

-- Ajout d'une contrainte de clé étrangère pour relier 'receiver_id' dans la table 'transactions' à 'id' dans 'users'
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_RECEIVER FOREIGN KEY (receiver_id) REFERENCES users (id);

-- Ajout d'une contrainte de clé étrangère pour relier 'sender_id' dans la table 'transactions' à 'id' dans 'users'
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);

-- Ajout d'une contrainte de clé étrangère dans 'users_connections' pour relier 'connections_id' au champ 'id' dans 'users'
ALTER TABLE users_connections
    ADD CONSTRAINT fk_usecon_on_connections FOREIGN KEY (connections_id) REFERENCES users (id);

-- Ajout d'une contrainte de clé étrangère dans 'users_connections' pour relier 'user_id' au champ 'id' dans 'users'
ALTER TABLE users_connections
    ADD CONSTRAINT fk_usecon_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- ajout user toto mot de passe = "s"
INSERT INTO `users` (`id`, `username`, `password`, `email`, `solde`) VALUES (1, 'toto', '$2a$10$jZxg/JoHtYxHtqZY5nS26.T/zNmZAYGlnNZWRUpuWTQWM8mHLuWZm', 'toto@toto.fr', 100.00);
-- ajout user tata mot de passe = "s"
INSERT INTO `users` (`id`, `username`, `password`, `email`, `solde`) VALUES (2, 'tata', '$2a$10$Z4kPq6LxwAYSSmLkT6Gt0.z8F.VQIWkyzpCrmmFWTbYHfIJ70Uuj6', 'tata@tata.fr', 100.00);
