package fr.paymybuddy.application.configuration;

import fr.paymybuddy.application.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register/**").permitAll() // Accès public pour s'enregistrer
                        .requestMatchers("/portail").permitAll() // Accès public
                        .anyRequest().authenticated() // Tout le reste nécessite une authentification
                )
                .formLogin(form -> form
                        .loginPage("/login") // Page de login personnalisée
                        .defaultSuccessUrl("/home", true) // Redirection après succès
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL pour la déconnexion
                        .logoutSuccessUrl("/portail?logout") // Redirection après déconnexion
                        .permitAll()
                )
                .csrf(httpSecurityCsrfConfigurer -> {
                    httpSecurityCsrfConfigurer.disable();
                });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Pour hacher les mots de passe
    }
}