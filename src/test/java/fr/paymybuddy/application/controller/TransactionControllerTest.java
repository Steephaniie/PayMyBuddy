package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.CustomUserDetailsService;
import fr.paymybuddy.application.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de test unitaire pour le contrôleur TransactionController.
 * Cette classe vérifie les fonctionnalités liées à la gestion des transactions
 * en simulant des interactions HTTP avec le contrôleur.
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TransactionController transactionController;

    /**
     * Teste la création réussie d'une transaction.
     * Scénario : un utilisateur valide soumet une transaction avec un contact existant,
     * un montant correct et une description. Le service renvoie un résultat positif.
     *
     * @throws Exception en cas d'erreur.
     */
    @Test
    void testCreateTransaction_Success() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("userTest");
        mockUser.setEmail("user@test.com");
        mockUser.setId(1L);

        Long contactId = 1L; // Identifiant du contact pour la transaction
        String description = "Test transaction";
        double amount = 100.0;
        when(transactionService.createTransaction(any(User.class), eq(contactId), eq(amount), eq(description)))
                .thenReturn(true);

        // Act & Assert
        ResponseEntity<Boolean> a = transactionController.createTransaction(mockUser, contactId, description, amount);
        assert a.getStatusCode().is2xxSuccessful();
        verify(transactionService, times(1))
                .createTransaction(any(User.class), eq(contactId), eq(amount), eq(description));
    }

    /**
     * Teste un échec lors de la création d'une transaction.
     * Scénario : un utilisateur valide soumet une transaction, mais une erreur survient
     * pendant le traitement. Le service renvoie une exception, et une réponse d'erreur est attendue.
     *
     * @throws Exception en cas d'erreur.
     */
    @Test
    void testCreateTransaction_Failure() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("userTest");
        mockUser.setEmail("user@test.com");
        mockUser.setId(1L);

        Long contactId = 1L;
        String description = "Test transaction";
        double amount = 100.0;
        when(transactionService.createTransaction(any(User.class), eq(contactId), eq(amount), eq(description)))
                .thenThrow(new RuntimeException("Erreur lors de la transaction")); // Simulation d'une erreur dans le service

        // Act & Assert
        ResponseEntity<Boolean> a = transactionController.createTransaction(mockUser, contactId, description, amount);
        assert a.getStatusCode().is5xxServerError();

        verify(transactionService, times(1))
                .createTransaction(any(User.class), eq(contactId), eq(amount), eq(description));
    }

    /**
     * Teste la récupération des transactions envoyées par un utilisateur.
     * Scénario : un utilisateur valide demande à voir ses transactions précédentes.
     * Le service renvoie une liste de transactions, et ces données sont validées.
     *
     * @throws Exception en cas d'erreur.
     */
    @Test
    void testGetTransactionsBySender_Success() throws Exception {
        // Préparation des données pour le test
        User user = new User(); // Utilisateur initiant les transactions
        user.setId(1L); // Identifiant de l'utilisateur
        user.setUsername("testUser"); // Nom d'utilisateur

        List<TransactionDTO> transactions = Arrays.asList(
                new TransactionDTO("user 1", "Description 1", 50.0),
                new TransactionDTO("user 2", "Description 2", 75.0)
        );

        when(transactionService.findTransactionsBySender(user.getId()))
                .thenReturn(transactions);

        ResponseEntity<List<TransactionDTO>> a = transactionController.getTransactionsBySender(user);
        assert a.getStatusCode().is2xxSuccessful();
        assert a.getBody().size() == 2;
        assert a.getBody().get(0).getAmount() == 50.0;


        verify(transactionService, times(1))
                .findTransactionsBySender(user.getId());
    }
}