package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class UserProfileController {

    private final UserService userService;

    /**
     * Affiche le formulaire de profil de l'utilisateur connecté.
     */
    @GetMapping("/profile")
    public String getUserProfile(@AuthenticationPrincipal User user, Model model) {
        // Récupération des informations de l'utilisateur connecté
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getEmail(), user.getId());
        model.addAttribute("userDTO", userDTO);
        return "profile";
    }

    /**
     * Met à jour le profil de l'utilisateur connecté.
     */
    @PostMapping("/profile")
    public String updateUserProfile(
            @AuthenticationPrincipal User userConnecte,
            String username,
            String email,
            String password,
            Model model) {
        // Appel au service pour effectuer la mise à jour
        User updatedUser = userService.majUser(userConnecte, username, password, email);
        UserDTO userDTO = new UserDTO(updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getId());

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("successMessage", "Profil mis à jour avec succès !");
        return "profile";
    }

    @GetMapping("/addContact")
    public String showAddContactPage(@AuthenticationPrincipal User user, Model model) {
        // Passez l'utilisateur connecté à la vue pour personnalisation (exemple : affichage de son nom)
        model.addAttribute("username", user.getUsername());
        return "addContact"; // Renvoie vers la vue `addContact.html`
    }

}