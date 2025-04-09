package fr.paymybuddy.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * Représente un utilisateur dans le système.
 * Cette entité est mappée à la table 'users' en base de données.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;


    /**
     * Nom d'utilisateur unique de l'utilisateur.
     */
    private String username;

    /**
     * Mot de passe associé à l'utilisateur.
     */
    private String password;

    /**
     * Adresse électronique de l'utilisateur.
     */
    private String email;

    /**
     * Liste des connexions de cet utilisateur
     * (autres utilisateurs auxquels il se connecte).
     */
    @OneToMany
    private List<User> connections;

    /**
     * Logger instance for structured logging.
     */
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    public User(String username, String email, String hashedPassword) {
        this.username = username;
        this.email=email;
        this.password = hashedPassword;
    }


    /**
     * Récupère les rôles ou autorisations associés à l'utilisateur.
     * Actuellement, aucun rôle n'est défini pour cet utilisateur.
     *
     * @return Une liste vide d'autorisations.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        logger.debug("Début de l'exécution de getAuthorities pour l'utilisateur avec l'identifiant : {}", id);
        try {
            Collection<? extends GrantedAuthority> authorities = List.of();
            logger.info("Les autorisations ont été récupérées avec succès pour l'utilisateur avec l'identifiant : {}", id);
            return authorities;
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la récupération des autorisations pour l'utilisateur avec l'identifiant : {}", id, e);
            throw e;
        }
    }

    /**
     * Vérifie si le compte de l'utilisateur n'est pas expiré.
     * Par défaut, le compte n'expire jamais.
     *
     * @return {@code true} car le compte est toujours actif.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Vérifie si le compte de l'utilisateur n'est pas verrouillé.
     * Par défaut, le compte n'est jamais verrouillé.
     *
     * @return {@code true} car le compte n'est jamais verrouillé.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Vérifie si les informations d'identification (mot de passe) de l'utilisateur n'ont pas expiré.
     * Par défaut, les informations d'identification ne sont jamais expirées.
     *
     * @return {@code true} car les informations d'identification sont toujours valides.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Vérifie si l'utilisateur est activé.
     * Par défaut, l'utilisateur est toujours activé.
     *
     * @return {@code true} car l'utilisateur est actif.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
