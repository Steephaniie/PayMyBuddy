package fr.paymybuddy.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principale de l'application.
 * Point d'entrée de l'application Spring Boot.
 */
@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Lance l'application Spring Boot.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        try {
			logger.debug("Démarrage de l'application PayMyBuddy en cours...");
			SpringApplication.run(Application.class, args);
			logger.info("L'application PayMyBuddy a été démarrée avec succès !");
		} catch (Exception e) {
			logger.error("Une erreur s'est produite lors du démarrage de l'application.", e);
		}
    }

}
