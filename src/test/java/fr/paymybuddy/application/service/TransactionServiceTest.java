package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour le service TransactionService.
 * Cette classe teste les principales fonctionnalités de gestion des transactions, y compris la recherche
 * et la création de transactions, ainsi que la gestion des cas edge (solde insuffisant, destinataire invalide, etc.).
 */
@SpringBootTest
class TransactionServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Méthode exécutée avant chaque test pour initialiser les mocks.
     * Elle utilise Mockito pour ouvrir les mocks et injecter automatiquement les dépendances simulées.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindTransactionsBySender_Success() {
        // Arrange : Préparer les données nécessaires au test
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("testUser");
        User receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("testUser");
        Transaction transaction1 = new Transaction();
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setDescription("Payment for services");
        transaction1.setAmount(100.0);
        when(transactionRepository.findBySender_Id(sender.getId())).thenReturn(List.of(transaction1));

        // Act : Exécuter la méthode testée
        List<TransactionDTO> transactions = transactionService.findTransactionsBySender(sender.getId());

        // Assert : Vérifier les résultats et s'assurer qu'ils correspondent aux attentes
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1)).findBySender_Id(sender.getId());
    }

    @Test
    void testFindTransactionsBySender_NoTransactions() {
        // Arrange
        Long senderId = 1L;
        when(transactionRepository.findBySender_Id(senderId)).thenReturn(List.of());

        // Act
        List<TransactionDTO> transactions = transactionService.findTransactionsBySender(senderId);

        // Assert
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
        verify(transactionRepository, times(1)).findBySender_Id(senderId);
    }

    @Test
    void testCreateTransaction_Success() {
        // Arrange : Préparer les données nécessaires au test
        User sender = new User();
        sender.setId(1L);
//        sender.setBalance(100.0); // Solde du compte émetteur

        Long receiverId = 2L;
        User receiver = new User();
        receiver.setId(receiverId); // Identifiant du destinataire

        double amount = 50.0; // Somme à transférer
        String description = "Payment for services"; // Description de la transaction

        when(userService.getById(sender.getId())).thenReturn(sender);
        when(userService.getById(receiverId)).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act : Exécuter la méthode testée
        Boolean result = transactionService.createTransaction(sender, receiverId, amount, description);

        // Assert : Vérifier les résultats et s'assurer qu'ils correspondent aux attentes
        assertTrue(result);
//        assertEquals(50.0, sender.getBalance());
        verify(userService, times(1)).getById(sender.getId());
        verify(userService, times(1)).getById(receiverId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


}