-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           8.0.30 - MySQL Community Server - GPL
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Listage de la structure de la base pour paymybuddy
DROP DATABASE IF EXISTS `paymybuddy`;
CREATE DATABASE IF NOT EXISTS `paymybuddy` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `paymybuddy`;

-- Listage de la structure de table paymybuddy. transactions
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `amount` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_TRANSACTIONS_ON_RECEIVER` (`receiver_id`),
  KEY `FK_TRANSACTIONS_ON_SENDER` (`sender_id`),
  CONSTRAINT `FK_TRANSACTIONS_ON_RECEIVER` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_TRANSACTIONS_ON_SENDER` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy.transactions : ~0 rows (environ)

-- Listage de la structure de table paymybuddy. users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy.users : ~2 rows (environ)
REPLACE INTO `users` (`id`, `username`, `password`, `email`) VALUES
	(1, 'stef', '$2a$10$SJ.8HFT7inIUM1J.3X5uaOe9U2MDkqT2CcLH3KUYrdudhuOu8wrxW', 'stef@stef.fr'),
	(2, 'toto', '$2a$10$oLubf/BgGE4lLPoX3Y0b2O2LdRQEAzAMCmFhSN0HUVugmY4EbctBG', 'toto@toto.fr');

-- Listage de la structure de table paymybuddy. users_connections
DROP TABLE IF EXISTS `users_connections`;
CREATE TABLE IF NOT EXISTS `users_connections` (
  `user_id` bigint NOT NULL,
  `connections_id` bigint NOT NULL,
  UNIQUE KEY `uc_users_connections_connections` (`connections_id`),
  KEY `fk_usecon_on_user` (`user_id`),
  CONSTRAINT `fk_usecon_on_connections` FOREIGN KEY (`connections_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_usecon_on_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy.users_connections : ~0 rows (environ)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
