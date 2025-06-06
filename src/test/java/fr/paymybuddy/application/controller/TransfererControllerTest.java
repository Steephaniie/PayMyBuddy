package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.TransactionService;
import fr.paymybuddy.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour le contrôleur TransfererController.
 * Cette classe vérifie les fonctionnalités liées à la visualisation des transactions
 * et à la création de paiements.
 */
@SpringBootTest
class TransfererControllerTest {
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private UserService userService;
    @MockBean
    private Model model;
    @Autowired
    private TransfererController transfererController;

    /**
     * Configuration initiale exécutée avant chaque test.
     * Cette méthode initialise les mocks pour les services et le modèle,
     * et instancie le contrôleur avec les dépendances simulées.
     */
    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        userService = Mockito.mock(UserService.class);
        model = Mockito.mock(Model.class);

        transfererController = new TransfererController(transactionService, userService);
    }

    /**
     * Test de la méthode viewTransactionsBySender() du contrôleur.
     * Vérifie qu'une vue correcte est retournée lorsque les transactions
     * sont récupérées avec succès pour un utilisateur donné.
     */
    @Test
    void testViewTransactionsBySender_Success() {
        // Arrange
        User userConnecte = new User();
        userConnecte.setId(1L);
        userConnecte.setUsername("testUser");

        List<TransactionDTO> mockTransactions = Arrays.asList(new TransactionDTO(), new TransactionDTO());
        List<UserDTO> mockContacts = Arrays.asList(new UserDTO(), new UserDTO());

        when(transactionService.findTransactionsBySender(userConnecte.getId())).thenReturn(mockTransactions);
        when(userService.getContacts(userConnecte)).thenReturn(mockContacts);
        when(userService.getById(userConnecte.getId())).thenReturn(userConnecte);

        // Act
        String viewName = transfererController.viewTransactionsBySender(userConnecte, model);

        // Assert
        assertEquals("Transferer", viewName);
        verify(model).addAttribute("contacts", mockContacts);
        verify(model).addAttribute("username", userConnecte.getUsername());
    }

    /**
     * Test de la méthode viewTransactionsBySender() lorsqu'une exception est levée.
     * Vérifie qu'en cas d'erreur lors de la récupération des transactions,
     * la vue "error" est retournée et aucun attribut n'est ajouté au modèle.
     */
    @Test
    void testViewTransactionsBySender_Exception() {
        // Arrange
        User userConnecte = new User();
        userConnecte.setId(1L);
        userConnecte.setUsername("testUser");

        when(transactionService.findTransactionsBySender(userConnecte.getId())).thenThrow(new RuntimeException("Error"));

        // Act
        String viewName = transfererController.viewTransactionsBySender(userConnecte, model);

        // Assert
        assertEquals("error", viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

    /**
     * Test de la méthode payer() du contrôleur.
     * Vérifie qu'une transaction est créée avec succès et que la vue correcte est retournée.
     */
    @Test
    void testPayer_Success() {
        // Arrange
        User userConnecte = new User();
        userConnecte.setId(1L);
        userConnecte.setUsername("testUser");

        Long idContact = 2L;
        String description = "Test transaction";
        BigDecimal montant =  new BigDecimal("100");

        TransactionService.TransactionResult retour = new TransactionService.TransactionResult(true, "");

        when(transactionService.createTransaction(userConnecte, idContact, montant, description)).thenReturn(retour);
        when(userService.getById(userConnecte.getId())).thenReturn(userConnecte);
        // Act
        String viewName = transfererController.payer(userConnecte, model, idContact, description, montant);

        // Assert
        assertEquals("Transferer", viewName);
        verify(transactionService).createTransaction(userConnecte, idContact, montant, description);
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }

}