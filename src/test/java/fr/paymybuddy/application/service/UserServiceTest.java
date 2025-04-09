package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.UserDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour le service UserService.
 * Cette classe contient des tests unitaires pour vérifier le comportement des méthodes
 * selon différents scénarios.
 * Les tests incluent la recherche d'utilisateur, l'enregistrement, la mise à jour,
 * ainsi que les fonctionnalités d'ajout et de récupération de contacts.
 */
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    /**
     * Initialise les mocks avant chaque test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Teste la méthode {@link UserService#findUserByEmail(String)}.
     * Scénario : l'utilisateur recherché existe dans la base de données.
     * Vérifie que l'utilisateur renvoyé a un email correspondant à celui recherché.
     */
    @Test
    void testFindUserByEmail_UserExists() {
        // Préparation des données de test : création d'un utilisateur fictif avec email défini
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        // Simule la réponse du UserRepository pour l'email donné
        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        // Exécution de la méthode testée
        User result = userService.findUserByEmail(email);

        // Vérifications des résultats
        assertNotNull(result, "L'utilisateur ne doit pas être nul.");
        assertEquals(email, result.getEmail(), "L'e-mail de l'utilisateur doit correspondre.");
        verify(userRepository, times(1)).findByEmail(email);
    }

    /**
     * Teste la méthode findUserByEmail lorsque l'utilisateur n'existe pas dans la base de données.
     * Vérifie qu'une exception IllegalArgumentException est levée.
     */
    @Test
    void testFindUserByEmail_UserDoesNotExist() {
        // Préparation des données de test
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        assertNull( userService.findUserByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    /**
     * Teste la méthode saveUser pour vérifier que l'utilisateur est correctement sauvegardé.
     * Vérifie que l'utilisateur sauvegardé a les informations attendues.
     */
    @Test
    void testSaveUser() {
        // Préparation des données de test
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Teste la méthode {@link UserService#registerUser(String, String, String)}.
     * Scénario : l'enregistrement réussit car l'email n'existe pas encore dans la base.
     * Vérifie que l'utilisateur est enregistré avec un mot de passe encodé.
     */
    @Test
    void testRegisterUser_Success() {
        // Préparation des données de test : nouvel utilisateur
        String username = "newUser";
        String password = "password";
        String email = "newuser@example.com";

        // Simule l'absence d'un utilisateur existant avec cet email et l'encodage du mot de passe
        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(null));
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        // Utilisateur prévu après enregistrement
        User newUser = new User(username, email, "hashedPassword");

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Exécution de la méthode testée
        userService.registerUser(username, password, email);

        // Vérifications des interactions sur les dépendances
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Teste la méthode registerUser lorsque l'email existe déjà dans la base de données.
     * Vérifie qu'une exception IllegalArgumentException est levée et qu'aucun utilisateur n'est sauvegardé.
     */
    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Préparation des données de test
        String username = "newUser";
        String password = "password";
        String email = "existing@example.com";
        User existingUser = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(username, password, email));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(password);
    }

    /**
     * Teste la méthode {@link UserService#majUser(User, String, String, String)}.
     * Scénario : l'utilisateur existant est mis à jour avec de nouvelles informations.
     * Vérifie que les détails de l'utilisateur sont mis à jour comme attendu.
     */
    @Test
    void testMajUser() {
        // Préparation des données : utilisateur existant avec nom et email à modifier
        User existingUser = new User();
        existingUser.setUsername("oldUser");
        existingUser.setEmail("old@example.com");

        // Nouvelles informations pour la mise à jour
        String newUsername = "newUser";
        String newPassword = "newPassword";
        String newEmail = "new@example.com";

        // Simule l'encodage du nouveau mot de passe
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedNewPassword");

        // Simule l'utilisateur mis à jour après sauvegarde
        User updatedUser = new User();
        updatedUser.setUsername(newUsername);
        updatedUser.setEmail(newEmail);
        updatedUser.setPassword("hashedNewPassword");

        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // Exécution de la méthode testée
        User result = userService.majUser(existingUser, newUsername, newPassword, newEmail);

        // Vérifications des résultats obtenus
        assertNotNull(result, "L'utilisateur mis à jour ne doit pas être nul.");
        assertEquals(newUsername, result.getUsername(), "Le nom d'utilisateur doit correspondre.");
        assertEquals(newEmail, result.getEmail(), "Le nouvel email doit correspondre.");
        assertEquals("hashedNewPassword", result.getPassword(), "Le mot de passe encodé doit correspondre.");
        verify(userRepository, times(1)).save(existingUser);
    }

    /**
     * Teste l'ajout d'un contact (addContact).
     * Vérifie que l'utilisateur cible est correctement ajouté aux connexions de l'utilisateur actuel.
     */
//    @Test
//    void testAddContact_Success() {
//        // Préparation des utilisateurs (actuel et cible)
//        User currentUser = new User();
//        currentUser.setUsername("currentUser");
//
//        User targetUser = new User();
//        targetUser.setUsername("targetUser");
//
//        // Act
//        userService.addContactByUser(currentUser, targetUser);
//
//        // Assert
//        assertTrue(currentUser.getConnections().contains(targetUser));
//        verify(userRepository, times(1)).save(currentUser);
//    }

    /**
     * Teste la récupération de la liste des contacts (getContacts).
     * Vérifie que la liste des connexions de l'utilisateur est correctement convertie en DTO.
     */
    @Test
    void testGetContacts() {
        // Préparation de l'utilisateur et de ses connexions
        User user = new User();
        user.setId(1L);
        User contact1 = new User();
        contact1.setUsername("contact1");
        User contact2 = new User();
        contact2.setUsername("contact2");
        user.setConnections(List.of(contact1,contact2));
        when(userRepository.getById(user.getId())).thenReturn(user);
        // Act
        List<UserDTO> userDTOList = userService.getContacts(user);

        // Assert
        assertEquals(2, userDTOList.size());
        assertEquals("contact1", userDTOList.get(0).getUsername());
        assertEquals("contact2", userDTOList.get(1).getUsername());
    }
}

    