package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrôleur responsable de la gestion de l'inscription des utilisateurs.
 * Fournit les points d'entrée pour afficher le formulaire d'inscription
 * et enregistrer un nouvel utilisateur.
 */
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * Affiche le formulaire d'inscription pour les nouveaux utilisateurs.
     *
     * @return Le nom du fichier HTML correspondant à la page d'inscription.
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        logger.debug("Exécution de la méthode showRegisterForm");
        return "Register"; // Retourne le nom du fichier HTML (sans .html)
    }

    /**
     * Enregistre un nouvel utilisateur avec les informations fournies.
     * En cas de succès, l'utilisateur est redirigé vers la page de connexion.
     * En cas d'erreur, un message explicatif est renvoyé à la vue.
     *
     * @param username Nom d'utilisateur saisi par l'utilisateur.
     * @param password Mot de passe saisi par l'utilisateur.
     * @param email    Adresse email fournie lors de l'inscription.
     * @param model    Modèle permettant de transmettre des données à la vue.
     * @return Le nom de la vue à afficher ou une redirection vers une page spécifique.
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model) {
        logger.debug("Exécution de registerUser avec les paramètres - nom d'utilisateur : {}, email : {}", username, email);
        try {
            // Enregistrement de l'utilisateur en utilisant le service dédié.
            userService.registerUser(username, password, email);
            logger.info("Utilisateur enregistré avec succès - nom d'utilisateur : {}", username);
            return "redirect:/login"; // Redirige vers la page de login après succès
        } catch (Exception e) {
            // Gestion des erreurs : un message d'erreur est ajouté au modèle
            logger.error("Une erreur s'est produite lors de l'enregistrement de l'utilisateur - message : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            return "Register"; // Retourne à la page d'inscription avec un message d'erreur
        }
    }
}