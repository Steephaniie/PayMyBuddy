package fr.paymybuddy.application.configuration;

import fr.paymybuddy.application.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour l'application.
 * Cette classe configure l'authentification, la gestion des connexions,
 * les autorisations et la sécurité CSRF.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    /**
     * Configure la chaîne de filtres de sécurité HTTP.
     * Cette méthode définit les règles d'autorisation, la gestion des connexions
     * et la désactivation de la protection CSRF pour simplifier certains flux.
     *
     * @param http Configuration de la sécurité HTTP.
     * @return Le filtre de sécurité configuré.
     * @throws Exception Si une erreur survient lors de la configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Associe le service d'utilisateurs personnalisé
                .userDetailsService(customUserDetailsService)
                // Définit les règles d'autorisation
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register/**").permitAll() // Accès public pour s'enregistrer
                        .requestMatchers("/portail").permitAll() // Accès public
                        .anyRequest().authenticated() // Tout le reste nécessite une authentification
                )
                // Configure la gestion des formulaires de connexion
                .formLogin(form -> form
                        .loginPage("/login") // Page de login personnalisée
                        .defaultSuccessUrl("/home", true) // Redirection après succès
                        .permitAll()
                )
                // Configure la déconnexion
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL pour la déconnexion
                        .logoutSuccessUrl("/portail?logout") // Redirection après déconnexion
                        .permitAll()
                )
                // Désactive la protection CSRF (par exemple, pour des tests simplifiés)
                .csrf(httpSecurityCsrfConfigurer -> {
                    httpSecurityCsrfConfigurer.disable();
                });
        logger.info("Chaîne de filtres de sécurité configurée avec succès.");
        return http.build();
    }

    /**
     * Crée un encodeur de mots de passe basé sur BCrypt.
     * Cet encodeur est utilisé pour hacher et vérifier les mots de passe utilisateur de manière sécurisée.
     *
     * @return Un encodeur BCrypt pour hacher les mots de passe.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("Création du bean BCryptPasswordEncoder pour le hachage des mots de passe.");
        return new BCryptPasswordEncoder(); // Pour hacher les mots de passe
    }
}