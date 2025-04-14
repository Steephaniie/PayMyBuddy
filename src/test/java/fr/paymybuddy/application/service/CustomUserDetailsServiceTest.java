package fr.paymybuddy.application.service;

import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour le service CustomUserDetailsService.
 * Cette classe vérifie que le service charge correctement les détails de l'utilisateur
 * ou lève une exception lorsque l'utilisateur n'existe pas.
 */
@SpringBootTest
class CustomUserDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configure les mocks avant chaque test.
     */
    @BeforeEach
    void setUp() {
        // Initialisation des objets annotés avec @Mock et @InjectMocks
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Teste le chargement des détails d'un utilisateur existant.
     * Vérifie que le service renvoie les bonnes informations pour un utilisateur valide.
     */
    @Test
    void loadUserByUsername_Success() {
        // Données de départ (Given)
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("password"); // Mot de passe simulé
        when(userRepository.getByEmail(username)).thenReturn(Optional.of(mockUser)); // Mock du UserRepository

        // Action testée (When)
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Vérifications (Then)
        assertEquals(username, userDetails.getUsername(), "Le nom d'utilisateur devrait correspondre.");
        verify(userRepository, times(1)).getByEmail(username); // Vérifie l'appel au UserRepository
    }

    /**
     * Teste le comportement du service lorsqu'un utilisateur est introuvable.
     * Vérifie que UsernameNotFoundException est levée.
     */
    @Test
    void loadUserByUsername_UserNotFound() {
        // Données de départ (Given)
        String email = "unknownuser";
        when(userRepository.getByEmail(email)).thenReturn(Optional.empty()); // Simule l'absence de l'utilisateur

        // Action testée et vérification (When & Then)
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email),
                "Une exception devrait être levée pour un utilisateur inexistant.");
        verify(userRepository, times(1)).getByEmail(email); // Vérifie l'appel à UserRepository
    }
}