package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 * Permet de créer, lire, mettre à jour et supprimer des utilisateurs via des requêtes HTTP.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Cryptez le mot de passe
        userService.saveUser(user);
        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }


    /**
     * Constructeur pour l'injection du UserService.
     *
     * @param userService Service pour gérer les utilisateurs.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint pour récupérer un utilisateur par son identifiant.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return L'utilisateur trouvé ou une réponse 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) {
        Optional<User> user = userService.findUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour trouver un utilisateur par son email.
     *
     * @param email Adresse email de l'utilisateur.
     * @return L'utilisateur trouvé ou une réponse 404.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        Optional<User> user = userService.findUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour trouver un utilisateur par son nom d'utilisateur (username).
     *
     * @param username Nom d'utilisateur.
     * @return L'utilisateur trouvé ou une réponse 404.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour créer un nouvel utilisateur.
     *
     * @param user Objet utilisateur à créer.
     * @return L'utilisateur créé avec un statut HTTP 201.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Endpoint pour mettre à jour les informations d'un utilisateur.
     *
     * @param userId Identifiant de l'utilisateur à mettre à jour.
     * @param user   Nouvel objet utilisateur.
     * @return L'utilisateur mis à jour ou 404 si l'utilisateur n'existe pas.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long userId, @RequestBody User user) {
        Optional<User> existingUser = userService.findUserById(userId);
        if (existingUser.isPresent()) {
            user.setId(userId);
            User updatedUser = userService.saveUser(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint pour lister tous les utilisateurs.
     *
     * @return La liste de tous les utilisateurs.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint pour supprimer un utilisateur par son identifiant.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return Une réponse 204 (no content) ou 404 si l'utilisateur n'existe pas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        Optional<User> existingUser = userService.findUserById(userId);
        if (existingUser.isPresent()) {
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}