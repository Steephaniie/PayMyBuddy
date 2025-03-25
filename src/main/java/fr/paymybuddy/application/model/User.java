package fr.paymybuddy.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changement ici
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
