package fr.paymybuddy.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur Spring MVC pour gérer les routes principales de l'application.
 * Ce contrôleur gère les redirections vers les pages statiques telles que l'accueil, la connexion et le portail.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Gère la route GET pour la page de connexion.
     *
     * @return le nom de la vue correspondante pour la connexion (Login.html).
     */
    @GetMapping("/login")
    public String login() {
        logger.debug("Traitement de la requête GET pour la route /login.");
        try {
            // Retourne la vue de connexion
            logger.info("Accès réussi à la page de connexion.");
            return "Login";
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la page de connexion.", e);
            throw e;
        }
    }

    /**
     * Gère la route GET pour la page du portail utilisateur.
     *
     * @return le nom de la vue correspondante pour le portail (Portail.html).
     */
    @GetMapping("/portail")
    public String portail() {
        logger.debug("Traitement de la requête GET pour la route /portail.");
        try {
            // Retourne la vue du portail
            logger.info("Accès réussi à la page du portail.");
            return "Portail";
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la page du portail.", e);
            throw e;
        }
    }

}