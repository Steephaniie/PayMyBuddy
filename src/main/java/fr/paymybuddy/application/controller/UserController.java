package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Affiche le formulaire de profil de l'utilisateur actuellement connecté.
     *
     * @param userConnecte L'utilisateur actuellement connecté.
     * @param model        Modèle permettant de transmettre les détails du profil à la vue.
     * @return Le nom de la vue `Profil.html` affichant les détails du profil.
     */
    @GetMapping("/profil")
    public String getUserProfile(@AuthenticationPrincipal User userConnecte, Model model) {
        logger.debug("Début de la méthode getUserProfile pour l'utilisateur : {}", userConnecte.getUsername());

        // Récupération des informations de l'utilisateur connecté
        UserDTO userDTO = new UserDTO(userConnecte.getUsername(), userConnecte.getEmail(), userConnecte.getId());
        model.addAttribute("userDTO", userDTO);

        logger.debug("Profil utilisateur chargé avec succès pour l'utilisateur : {}", userConnecte.getUsername());
        return "Profil";
    }

    /**
     * Met à jour le profil de l'utilisateur connecté.
     *
     * @param userConnecte L'utilisateur connecté actuellement authentifié.
     * @param username     Le nouveau nom d'utilisateur.
     * @param email        Le nouvel email de l'utilisateur.
     * @param password     Le nouveau mot de passe.
     * @param model        Modèle permettant de transmettre des attributs à la vue.
     * @return Le nom de la vue `Profil.html` après mise à jour.
     */
    @PostMapping("/profil")
    public String updateUserProfile(
            @AuthenticationPrincipal User userConnecte,
            String username,
            String email,
            String password,
            Model model) {

        logger.debug("Début de la méthode updateUserProfile pour l'utilisateur : {}", userConnecte.getUsername());

        try {
            // Appel au service pour effectuer la mise à jour
            User updatedUser = userService.majUser(userConnecte, username, password, email);
            UserDTO userDTO = new UserDTO(updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getId());

            model.addAttribute("userDTO", userDTO);
            model.addAttribute("succes", "Profil mis à jour avec succès !");

            logger.info("Profil utilisateur mis à jour avec succès pour l'utilisateur : {}", updatedUser.getUsername());
            return "Profil";
        } catch (Exception e) {
            logger.error("Une erreur s'est produite lors de la mise à jour du profil pour l'utilisateur : {}", userConnecte.getUsername(), e);
            model.addAttribute("error", "Une erreur s'est produite lors de la mise à jour du profil.");
            return "Profil";
        }
    }

    /**
     * Affiche la page contenant le formulaire d'ajout d'un nouveau contact.
     *
     * @param userConnecte L'utilisateur actuellement connecté.
     * @param model        Modèle permettant de transmettre des données à la vue.
     * @return Le nom de la vue `Contact.html` affichant le formulaire.
     */
    @GetMapping("/contact")
    public String showAddContactPage(@AuthenticationPrincipal User userConnecte, Model model) {
        logger.debug("Début de la méthode showAddContactPage pour l'utilisateur : {}", userConnecte.getUsername());

        // Passez l'utilisateur connecté à la vue pour personnalisation (exemple : affichage de son nom)
        model.addAttribute("username", userConnecte.getUsername());

        logger.debug("Page d'ajout de contact chargée avec succès pour l'utilisateur : {}", userConnecte.getUsername());
        return "Contact"; // Renvoie vers la vue `Contact.html`
    }

    /**
     * Ajoute un nouvel utilisateur à la liste des contacts de l'utilisateur connecté.
     *
     * @param userConnecte L'utilisateur actuellement connecté et authentifié.
     * @param email        Adresse email du contact à ajouter.
     * @param model        Modèle permettant de transmettre des messages d'erreur ou de succès à la vue.
     * @return Le nom de la vue `Contact.html` avec la liste mise à jour ou un message d'erreur.
     */
    @PostMapping("/contact")
    public String addContact(
            @AuthenticationPrincipal User userConnecte,
            @RequestParam String email,
            Model model
            ) {
        logger.debug("Début de la méthode contact pour l'utilisateur : {}", userConnecte.getUsername());

        if(email.equals(userConnecte.getEmail())) {
            model.addAttribute("error", "Vous ne pouvez pas vous ajouter comme contacts !");
            return "Contact"; // Renvoie vers la vue `Contact.html`;
        }
        // Récupération des contacts de l'utilisateur connecté
        UserDTO usersContacts = userService.addContactByEmail(userConnecte, email);
        if (usersContacts != null) {
            logger.info("Contact avec l'email '{}' ajouté avec succès pour l'utilisateur : {}", email, userConnecte.getUsername());
            model.addAttribute("succes", "Contact avec l'email "+email+"  ajouté avec succès");
            return "Contact"; // Renvoie vers la vue `Contact.html`;
        } else {
            logger.error("Échec de l'ajout du contact pour l'utilisateur : {} avec l'email : {}", userConnecte.getUsername(), email);
            model.addAttribute("error", "Ce contact n'existe pas ");
            return  "Contact"; // Renvoie vers la vue `Contact.html`;
        }
    }

}