package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Classe de tests pour le contrôleur RegisterController.
 * Cette classe utilise Mockito pour simuler les dépendances et tester le comportement du contrôleur.
 */
@SpringBootTest
class RegisterControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private Model model;

    @Autowired
    private RegisterController registerController;

    /**
     * Méthode exécutée avant chaque test pour initialiser les mocks avec Mockito.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Teste la méthode showRegisterForm.
     * Vérifie que l'affichage du formulaire d'inscription renvoie la vue correcte "register".
     */
    @Test
    void testShowRegisterForm() {
        // Act - Appel de la méthode à tester
        String viewName = registerController.showRegisterForm();

        // Assert - Vérifie que la vue retournée est "register"
        assertEquals("Register", viewName, "La vue devrait être 'register'.");
    }

    /**
     * Teste le scénario où l'inscription d'un utilisateur réussit.
     * Vérifie que l'utilisateur est redirigé vers la page de connexion et que le service d'inscription est appelé correctement.
     */
    @Test
    void testRegisterUserSuccess() throws Exception {
        // Arrange - Prépare les données d'entrée et configure le mock du service
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";

        doNothing().when(userService).registerUser(username, password, email);

        // Act - Appel de la méthode registerUser du contrôleur
        String viewName = registerController.registerUser(username, password, email, model);

        // Assert - Vérifie que la vue retournée est "redirect:/login" et que le service a été appelé une fois
        assertEquals("redirect:/login", viewName, "L'utilisateur devrait être redirigé vers la vue de connexion.");
        verify(userService, times(1)).registerUser(username, password, email);
        verifyNoInteractions(model);
    }

    /**
     * Teste le scénario où l'inscription d'un utilisateur échoue.
     * Vérifie que l'utilisateur reste sur la page d'inscription avec un message d'erreur approprié.
     */
    @Test
    void testRegisterUserFailure() throws Exception {
        // Arrange - Prépare les données d'entrée, le message d'erreur et configure le mock du service pour lever une exception
        String username = "testUser";
        String password = "testPassword";
        String email = "invalid-email";
        String errorMessage = "Invalid email format";

        doThrow(new IllegalArgumentException(errorMessage))
                .when(userService).registerUser(username, password, email);

        // Act - Appel de la méthode registerUser du contrôleur avec les données en échec
        String viewName = registerController.registerUser(username, password, email, model);

        // Assert - Vérifie que la vue retournée est "register", et que le message d'erreur a été ajouté au modèle
        assertEquals("Register", viewName, "L'utilisateur devrait rester sur la page d'inscription en cas d'erreur.");
        verify(userService, times(1)).registerUser(username, password, email);
        verify(model, times(1)).addAttribute("error", "Erreur lors de l'inscription : " + errorMessage);
    }
}