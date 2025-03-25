package fr.paymybuddy.application.service;

import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs.
 * Fournit des méthodes pour interagir avec le dépôt des utilisateurs
 * et implémente la logique métier associée.
 */
@Service
public class UserService {

    /**
     * Le dépôt pour les entités utilisateurs.
     */
    private final UserRepository userRepository;

    /**
     * Constructeur pour l'injection du UserRepository.
     *
     * @param userRepository Le dépôt des utilisateurs.
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Enregistre ou met à jour un utilisateur dans le dépôt.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré ou mis à jour.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param userId L'identifiant de l'utilisateur à supprimer.
     */
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }


    /**
     * Recherche un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Récupère la liste de tous les utilisateurs du dépôt.
     *
     * @return Une liste contenant tous les utilisateurs.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Ce nom est déjà pris.");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hachage du mot de passe
        userRepository.save(user);
    }


}