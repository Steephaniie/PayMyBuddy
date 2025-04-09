package fr.paymybuddy.application.service;

import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service personnalisé pour récupérer les détails d'un utilisateur enregistré.
 * Implémente l'interface {@link UserDetailsService} pour intégrer les fonctionnalités de gestion des utilisateurs
 * avec le mécanisme d'authentification de Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * Charge un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'utilisateur à rechercher.
     * @return Un objet {@link UserDetails} représentant l'utilisateur trouvé.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé pour le nom d'utilisateur donné.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Tentative de recherche de l'utilisateur avec le nom d'utilisateur : {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("L'utilisateur avec le nom d'utilisateur '{}' est introuvable.", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé : " + username);
                });
        logger.info("L'utilisateur avec le nom d'utilisateur '{}' a été chargé avec succès.", username);
        return user;
    }
}
