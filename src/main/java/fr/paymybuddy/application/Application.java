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
    public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
    }
}
