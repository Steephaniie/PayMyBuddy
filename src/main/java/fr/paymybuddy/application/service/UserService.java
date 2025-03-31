package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@AllArgsConstructor
public class UserService {

    /**
     * Le dépôt pour les entités utilisateurs.
     */
    private final UserRepository userRepository;


    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
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


    public User majUser(User user, String username, String password, String email) {
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hachage du mot de passe
        return userRepository.save(user);
    }

    public void addContact(User currentUser, User targetUser) {
        List<User> connections = currentUser.getConnections();

        // Évitez les doublons
        if (!connections.contains(targetUser)) {
            connections.add(targetUser);
            userRepository.save(currentUser); // Met à jour les connexions de l'utilisateur
        }
    }

    public List<UserDTO> getContacts(User user) {
        // le user connecté n'est pas entièrement chargée - pas de connexion
       User currentUser = userRepository.getById(user.getId());
       List<User> connections = currentUser.getConnections();
        // Transformation des entités User en UserDTO
        List<UserDTO> contacts = connections.stream()
                .map(connection -> new UserDTO(connection.getUsername(), connection.getEmail(), connection.getId()))
                .toList();
        return contacts;
        // Retourner la réponse avec les contacts au format JSON
    }

    public UserDTO addContacts(User user, String email) {
        // le user connecté n'est pas entièrement chargée - pas de connexion
        User currentUser = userRepository.getById(user.getId());
        // Vérifiez si l'email cible correspond à un utilisateur existant
        User nouveauContact = findUserByEmail(email);
        if (nouveauContact == null) {
            // Si l'utilisateur cible n'existe pas, retournez une erreur 404
            return null;
        }
        List<User> connections = currentUser.getConnections();
        // Évitez les doublons
        if (!connections.contains(nouveauContact)) {
            connections.add(nouveauContact);
            userRepository.save(currentUser); // Met à jour les connexions de l'utilisateur
        }

        // Créez un UserDTO avec le user crée
        return new UserDTO(nouveauContact.getUsername(), nouveauContact.getEmail(),nouveauContact.getId());
    }

    public User getById(Long receiver) {
        return userRepository.getById(receiver);
    }
}