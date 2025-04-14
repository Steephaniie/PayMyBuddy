package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service pour la gestion des utilisateurs.
 * Fournit des méthodes pour interagir avec le dépôt des utilisateurs
 * et implémente la logique métier associée.
 */
@Service
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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


    /**
     * Gestionnaire pour encoder les mots de passe des utilisateurs
     * (hashage sécurisé avant enregistrement).
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Enregistre un nouvel utilisateur après validation.
     * Vérifie si le nom d'utilisateur est disponible, hache le mot de passe, et le sauvegarde.
     *
     * @param username Le nom d'utilisateur à enregistrer.
     * @param password Le mot de passe de l'utilisateur.
     * @param email    L'adresse e-mail de l'utilisateur.
     * @throws IllegalArgumentException si le nom d'utilisateur est déjà pris.
     */
    public void registerUser(String username, String password, String email) {
        logger.info("Tentative d'enregistrement d'un nouvel utilisateur");
        logger.debug("Vérification si le nom d'utilisateur '{}' est déjà pris", username);
        email = email.trim();
        if (userRepository.getByEmail(email).isPresent()) {
            logger.error("Échec de l'enregistrement : cet email '{}' est déjà utilisé", username);
            throw new IllegalArgumentException("Cet email est déjà pris.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        //TODO par defaut les nouveaux demarrent avec 100 €
        user.setSolde(new BigDecimal("100"));
        user.setPassword(passwordEncoder.encode(password)); // Hachage du mot de passe
        userRepository.save(user);
        logger.info("Utilisateur '{}' enregistré avec succès", username);
    }


    /**
     * Met à jour les informations d'un utilisateur existant.
     * Modifie le nom d'utilisateur, l'email, et hache le mot de passe avant de sauvegarder.
     *
     * @param user     L'utilisateur à mettre à jour.
     * @param username Le nouveau nom d'utilisateur.
     * @param password Le nouveau mot de passe.
     * @param email    Le nouvel e-mail.
     * @return L'utilisateur mis à jour.
     */
    public User majUser(User user, String username, String password, String email) {
        logger.info("Mise à jour de l'utilisateur avec l'ID {}", user.getId());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hachage du mot de passe

        User updatedUser = userRepository.save(user);
        logger.debug("Détails de l'utilisateur mis à jour : {}", updatedUser);
        return updatedUser;
    }


    /**
     * Récupère la liste des connexions d'un utilisateur sous forme de DTO.
     * Charge les connexions, puis les transforme en objets UserDTO.
     *
     * @param user L'utilisateur pour lequel les connexions sont récupérées.
     * @return Une liste d'objets UserDTO représentant les connexions.
     */
    public List<UserDTO> getContacts(User user) {
        logger.info("Récupération des contacts pour l'utilisateur avec l'ID {}", user.getId());
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

    /**
     * Ajoute un nouvel utilisateur comme contact en vérifiant son existence
     * et évite les doublons. Retourne un DTO de l'utilisateur ajouté.
     *
     * @param user  L'utilisateur actuel qui ajoute un contact.
     * @param email L'email de l'utilisateur cible à ajouter comme contact.
     * @return Un UserDTO pour le contact ajouté, ou null si l'utilisateur cible n'existe pas.
     */
    public UserDTO addContactByEmail(User user, String email) {
        // le user connecté n'est pas entièrement chargée - pas de connexion
        User currentUser = userRepository.getById(user.getId());
        // Vérifiez si l'email cible correspond à un utilisateur existant
        User nouveauContact = findUserByEmail(email);
        if (nouveauContact == null) {
            // Si l'utilisateur cible n'existe pas, retournez une erreur 404
            return null;
        }
        // Vérifier si le nouveau contact est déjà dans la liste de contacts
        boolean contactExisteDeja = currentUser.getConnections().stream()
                .anyMatch(contact -> contact.getId().equals(nouveauContact.getId()));

        if (!contactExisteDeja) {
            // Ajouter le nouveau contact et sauvegarder les modifications
            currentUser.getConnections().add(nouveauContact);
            userRepository.save(currentUser);
        }

        List<User> connections = currentUser.getConnections();
        // Évitez les doublons
        if (!connections.contains(nouveauContact)) {
            connections.add(nouveauContact);
            userRepository.save(currentUser); // Met à jour les connexions de l'utilisateur
        }

        // Créez un UserDTO avec le user crée
        return new UserDTO(nouveauContact.getUsername(), nouveauContact.getEmail(), nouveauContact.getId());
    }

    /**
     * Récupère un utilisateur par son identifiant unique.
     *
     * @param receiver L'ID de l'utilisateur à récupérer.
     * @return L'utilisateur correspondant à l'ID fourni.
     */
    public User getById(Long receiver) {
        return userRepository.getById(receiver);
    }
}