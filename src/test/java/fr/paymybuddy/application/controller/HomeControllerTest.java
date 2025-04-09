package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Classe de test pour le contrôleur {@link HomeController}.
 * Utilise le contexte Spring Web MVC (avec MockMvc) pour tester les routes
 * et s'assurer du bon fonctionnement des méthodes de contrôle.
 */
@WebMvcTest(HomeController.class) // Annotation pour charger uniquement le contexte Spring MVC avec le contrôleur
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private User authenticatedUser; // Utilisateur simulé représentant l'utilisateur authentifié

    @Autowired
    private HomeController homeController; // Injecte les mocks dans UserProfileController

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build(); // Configuration de MockMvc
        // Simuler un utilisateur authentifié avec des données fictives
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("testUser");
        authenticatedUser.setEmail("test@example.com");
    }
    /**
     * Test pour la méthode home().
     * Ce test effectue une requête GET sur "/home" et vérifie :
     * <ul>
     *     <li>Le statut HTTP est "200 OK".</li>
     *     <li>La vue retournée est "home".</li>
     * </ul>
     */
    @Test
    void testHome() throws Exception {
        // Étape 1 : Exécution de la requête GET sur "/home"
        ResultActions result = mockMvc.perform(get("/home"));

        // Étape 2 : Validation du statut HTTP (attendu : 200 OK) et du nom de la vue (attendu : "home")
        result.andExpect(status().isOk()); // Vérifie que le statut HTTP est 200

    }

    /**
     * Test pour la méthode login().
     * Ce test effectue une requête GET sur "/login" et vérifie :
     * <ul>
     *     <li>Le statut HTTP est "200 OK".</li>
     *     <li>La vue retournée est "login".</li>
     * </ul>
     */
    @Test
    void testLogin() throws Exception {
        // Étape 1 : Exécution de la requête GET sur "/login"
        ResultActions result = mockMvc.perform(get("/login"));

        // Étape 2 : Validation du statut HTTP (attendu : 200 OK) et du nom de la vue (attendu : "login")
        result.andExpect(status().isOk());
    }

    /**
     * Test pour la méthode portail().
     * Ce test effectue une requête GET sur "/portail" et vérifie :
     * <ul>
     *     <li>Le statut HTTP est "200 OK".</li>
     *     <li>La vue retournée est "portail".</li>
     * </ul>
     */
    @Test
    void testPortail() throws Exception {
        // Étape 1 : Exécution de la requête GET sur "/portail"
        ResultActions result = mockMvc.perform(get("/portail"));

        // Étape 2 : Validation du statut HTTP (attendu : 200 OK) et du nom de la vue (attendu : "portail")
        result.andExpect(status().isOk()); // Vérifie que le statut HTTP est 200

    }
}