package fr.paymybuddy.application.repository;

import fr.paymybuddy.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Dépôt (Repository) pour la gestion des entités utilisateurs.
 * Fournit des méthodes pour effectuer des opérations CRUD sur les utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur si trouvé, ou vide sinon.
     */
    Optional<User> findByUsername(String username);

}