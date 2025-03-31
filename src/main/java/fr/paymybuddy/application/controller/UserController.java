package fr.paymybuddy.application.controller;

import org.springframework.ui.Model;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 * Permet de créer, lire, mettre à jour et supprimer des utilisateurs via des requêtes HTTP.
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Récupère les informations de l'utilisateur actuellement authentifié.
     *
     * @param user L'utilisateur actuellement authentifié, fourni par Spring Security.
     * @return Une réponse HTTP contenant un objet UserDTO avec les informations essentielles de l'utilisateur.
     */
    @GetMapping("")
    public ResponseEntity<UserDTO> getUser(@AuthenticationPrincipal User user) {
        // Créer le DTO à partir de l'utilisateur authentifié
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getEmail() , user.getId());

        // Renvoyer l'objet au format JSON
        return ResponseEntity.ok(userDTO);
    }


    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param userConnecte L'utilisateur actuellement authentifié.
     * @param username     Le nouveau nom d'utilisateur à attribuer.
     * @param password     Le nouveau mot de passe à attribuer.
     * @param email        La nouvelle adresse e-mail à attribuer.
     * @return Une réponse HTTP contenant l'objet UserDTO mis à jour.
     */
    @PutMapping("")
    public ResponseEntity<UserDTO> updateUser(
            @AuthenticationPrincipal User userConnecte,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email) {
        User updatedUser = userService.majUser(userConnecte, username, password, email);
        UserDTO userDTO = new UserDTO(updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getId());
        return ResponseEntity.ok(userDTO);

    }

    @PostMapping("/contact")
    public ResponseEntity<UserDTO> addContact(
            @AuthenticationPrincipal User user,
            @RequestParam String email) {

        // Récupération des contacts de l'utilisateur connecté
        UserDTO usersContacts = userService.addContacts(user, email);
        if (usersContacts != null)
            return ResponseEntity.ok(usersContacts);
        else
            return ResponseEntity.notFound().build();

    }


    @GetMapping("/contact")
    public ResponseEntity<List<UserDTO>> getContacts(@AuthenticationPrincipal User user) {

        // Récupération des contacts de l'utilisateur connecté
        List<UserDTO> usersContacts = userService.getContacts(user);

        return ResponseEntity.ok(usersContacts);
    }




}