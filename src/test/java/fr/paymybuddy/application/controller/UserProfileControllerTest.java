package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de test pour le contrôleur UserProfileController.
 * Utilise des tests unitaires avec mocks pour valider les fonctionnalités associées
 * à la gestion du profil utilisateur dans l'application.
 */
@SpringBootTest
class UserProfileControllerTest {

    private MockMvc mockMvc;

    private User authenticatedUser; // Utilisateur simulé représentant l'utilisateur authentifié

    @MockBean
    private UserService userService; // Mock du UserService

    @MockBean
    private Model model; // Mock du model pour les vues

    @Autowired
    private UserProfileController userProfileController; // Injecte les mocks dans UserProfileController

    /**
     * Initialise les mocks et l'objet MockMvc avant chaque test.
     * MockMvc permet de simuler les requêtes HTTP sur le contrôleur testé.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build(); // Configuration de MockMvc
        // Simuler un utilisateur authentifié avec des données fictives
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("testUser");
        authenticatedUser.setEmail("test@example.com");
    }

    /**
     * Teste la méthode GET pour afficher le profil utilisateur.
     * Vérifie que la vue "profile" est retournée avec l'attribut attendu dans le modèle.
     *
     * @throws Exception si une erreur se produit lors de la simulation de la requête.
     */
    @Test
    void testGetUserProfile() throws Exception {
        // Préparation d'un utilisateur fictif
        User mockUser = new User();
        mockUser.setUsername("userTest");
        mockUser.setEmail("user@test.com");
        mockUser.setId(1L);


        // Appel de la méthode et vérification des résultats
        mockMvc.perform(get("/profile")
                        .principal(mockUser::getUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("Profil"))
                .andExpect(model().attributeExists("userDTO"));

        // Vérifie que l'attribut "userDTO" a bien été ajouté au modèle
        verifyNoInteractions(model);
    }

    /**
     * Teste le scénario de succès de la mise à jour du profil utilisateur via la méthode POST.
     * Vérifie que les données mises à jour sont reflétées correctement dans la réponse.
     *
     * @throws Exception si une erreur survient lors de la simulation.
     */
    @Test
    void testUpdateUserProfileSuccess() throws Exception {

        String newUsername = "updatedUser";
        String newPassword = "updatedPassword";
        String newEmail = "updated@example.com";

        User user = new User(newUsername, newEmail, newPassword);
        when(userService.majUser(authenticatedUser, newUsername, newPassword, newEmail))
                .thenReturn(user);

        // Appeler la méthode testée
        String response = userProfileController.updateUserProfile(authenticatedUser, newUsername, newEmail,newPassword, model);

        assertEquals("Profil", response);
        // Vérifie que la méthode du service a été appelée avec les bons arguments
//        verify(userService).majUser(authenticatedUser, newUsername, newPassword, newEmail);
        verify(userService, times(1)).majUser(authenticatedUser, newUsername, newPassword, newEmail);
    }

    /**
     * Teste le scénario d'échec de la mise à jour du profil utilisateur lorsque le service lève une exception.
     * Vérifie que la vue "profile" est retournée avec un message d'erreur approprié.
     *
     * @throws Exception si une erreur survient lors de la simulation.
     */
    @Test
    void testUpdateUserProfileFailure() throws Exception {
        // Préparation de l'utilisateur
        User mockUser = new User();
        mockUser.setUsername("userTest");
        mockUser.setEmail("user@test.com");
        mockUser.setId(1L);

        // Simulation d'une exception lors de l'appel du service
        when(userService.majUser(any(User.class), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Erreur"));

        // Simulation de l'appel à la méthode POST /profile
        mockMvc.perform(post("/profile")
                        .flashAttr("userConnecte", mockUser) // Simulation de l'utilisateur connecté
                        .param("username", "newUsername")
                        .param("email", "new@test.com")
                        .param("password", "newPassword"))
                .andExpect(status().isOk()) // Vérifie que le statut de la réponse est 200 (OK)
                .andExpect(view().name("Profil")) // Vérifie que la vue retournée est "profile"
                .andExpect(model().attributeExists("errorMessage")) // Vérifie qu'un message d'erreur est présent
                .andExpect(model().attribute("errorMessage", "Une erreur s'est produite lors de la mise à jour du profil.")); // Vérifie le contenu du message d'erreur

        // Vérifie que la méthode du service a été appelée
//        verify(userService).majUser(eq(mockUser), eq("newUsername"), eq("newPassword"), eq("new@test.com"));
        verify(userService, times(1)).majUser(any(), eq("newUsername"), eq("newPassword"), eq("new@test.com"));
    }

    /**
     * Teste l'affichage de la page "Ajouter un contact".
     * Vérifie que l'utilisateur connecté est correctement affiché dans la vue "addContact".
     *
     * @throws Exception si une erreur se produit lors de la simulation.
     */
    @Test
    void testShowAddContactPage() throws Exception {
        // Préparation d'un utilisateur fictif
        User mockUser = new User();
        mockUser.setUsername("userTest");
        mockUser.setEmail("user@test.com");
        mockUser.setId(1L);

          // Simulation de l'appel à la méthode GET /addContact
        mockMvc.perform(get("/addContact")
                        .flashAttr("userConnecte", mockUser)
                .principal(mockUser::getUsername)) // Simulation de l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie que le statut de la réponse est 200 (OK)
                .andExpect(view().name("Contact")); // Vérifie que la vue retournée est "addContact"
    }

    @Test
    void testAddContactSuccess() throws Exception {

        String contactUsername = "updatedUser";
        String contactPassword = "updatedPassword";
        String contactEmail = "updated@example.com";

        UserDTO userDto = new UserDTO(contactUsername,contactEmail,2L);
        when(userService.addContactByEmail(authenticatedUser, contactEmail))
                .thenReturn(userDto);

        // Appeler la méthode testée
        ResponseEntity<UserDTO> a = userProfileController.addContact(authenticatedUser, contactEmail);
        assert a.getStatusCode().is2xxSuccessful();
        assert Objects.requireNonNull(a.getBody()).getUsername().equals(contactUsername);
        verify(userService, times(1)).addContactByEmail(authenticatedUser, contactEmail);
    }
}
