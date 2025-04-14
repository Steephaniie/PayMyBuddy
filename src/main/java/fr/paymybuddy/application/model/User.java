package fr.paymybuddy.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Column(nullable = false)
    @DecimalMin("0.00")
    @DecimalMax("9999999.99")
    @Digits(integer=7, fraction=2)

    private BigDecimal solde;
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
}
