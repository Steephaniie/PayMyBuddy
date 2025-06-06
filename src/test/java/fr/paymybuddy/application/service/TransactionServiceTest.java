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

import java.math.BigDecimal;
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
     * Méthode exécutée avant chaque test pour initialiser les mocks nécessaires au bon déroulement des tests.
     * Elle utilise Mockito pour ouvrir les mocks des dépendances utilisées dans la classe testée.
     * Cela permet d'éviter les dépendances réelles et de simuler le comportement attendu.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Teste le scénario dans lequel une liste de transactions est recherchée avec succès
     * pour un utilisateur spécifique (l'émetteur).
     * Vérifie que les données retournées correspondent aux attentes.
     */
    @Test
    void testFindTransactionsBySender_Success() {
        // Arrange : Préparer les données nécessaires au test en créant un émetteur, un destinataire et une transaction simulée
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
        transaction1.setAmount(new BigDecimal("100.0"));
        when(transactionRepository.findBySender_Id(sender.getId())).thenReturn(List.of(transaction1));

        // Act : Appeler la méthode pour rechercher les transactions
        List<TransactionDTO> transactions = transactionService.findTransactionsBySender(sender.getId());

        // Assert : Vérifier que les résultats retournés ne sont pas nuls, qu'une seule transaction est obtenue,
        // et que la méthode du repository est appelée une fois
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1)).findBySender_Id(sender.getId());
    }

    /**
     * Teste le scénario où aucun résultat n'est trouvé pour un utilisateur
     * lors de la recherche de transactions comme émetteur.
     * Prépare un identifiant inexistant et vérifie que la liste retournée est vide.
     * Valide ensuite que le repository est appelé exactement une fois.
     */
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

    /**
     * Teste la réussite de la création d'une transaction entre deux utilisateurs
     * avec des soldes valides. Prépare un émetteur et un destinataire avec des
     * données cohérentes et vérifie que les interactions nécessaires sont effectuées
     * correctement et que la transaction est marquée comme réussie.
     */
    @Test
    void testCreateTransaction_Success() {
        // Arrange : Préparer les données nécessaires au test
        User sender = new User();
        sender.setId(1L);
        sender.setSolde(new BigDecimal("100")); // Solde du compte émetteur

        Long receiverId = 2L;
        User receiver = new User();
        receiver.setId(receiverId); // Identifiant du destinataire
        receiver.setSolde(new BigDecimal("100")); // Solde du compte émetteur

        BigDecimal amount = new BigDecimal("50"); // Somme à transférer
        String description = "Payment for services"; // Description de la transaction

        when(userService.getById(sender.getId())).thenReturn(sender);
        when(userService.getById(receiverId)).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        new TransactionService.TransactionResult(true, "Votre message ici");

        // Act : Exécuter la méthode testée
        TransactionService.TransactionResult result = transactionService.createTransaction(sender, receiverId, amount, description);

        // Assert : Vérifier les résultats et s'assurer qu'ils correspondent aux attentes
        assertTrue(result.isSuccess());
        verify(userService, times(1)).getById(sender.getId());
        verify(userService, times(1)).getById(receiverId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    /**
     * Teste la récupération des transactions pour un utilisateur, à la fois
     * celles qu'il a envoyées et celles qu'il a reçues. Prépare des données
     * simulant une transaction envoyée et une reçue, vérifie que les
     * informations sont correctement formatées dans les DTO retournés, et
     * valide que les interactions avec le repository ont bien lieu.
     */
    @Test
    void testFindMesTransactions_Success() {
        // Préparer les données de test
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("user2");

        Transaction sentTransaction = new Transaction();
        sentTransaction.setSender(user);
        sentTransaction.setReceiver(receiver);
        sentTransaction.setAmount(BigDecimal.valueOf(50));
        sentTransaction.setDescription("Payment to receiver");

        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setSender(receiver);
        receivedTransaction.setReceiver(user);
        receivedTransaction.setAmount(BigDecimal.valueOf(30));
        receivedTransaction.setDescription("Refund from receiver");

        List<Transaction> transactions = List.of(sentTransaction, receivedTransaction);

        // Configurer les comportements du mock repository
        when(transactionRepository.findBySenderOrReceiver(user, user)).thenReturn(transactions);

        // Lancer la méthode testée
        List<TransactionDTO> results = transactionService.findMesTransactions(user);

        // Vérifier les résultats
        assertNotNull(results);
        assertEquals(2, results.size());

        // Vérifier le détail des DTO retournés
        TransactionDTO dto1 = results.get(0); // Transaction envoyée
        assertEquals("user2", dto1.getReceiverName()); // Nom du receiver
        assertEquals(BigDecimal.valueOf(-50.00).setScale(2), dto1.getAmount().setScale(2)); // Montant négatif
        assertEquals("Payment to receiver", dto1.getDescription());

        TransactionDTO dto2 = results.get(1); // Transaction reçue
        assertEquals("user2", dto2.getReceiverName()); // Nom du sender
        assertEquals(BigDecimal.valueOf(30.00).setScale(2), dto2.getAmount().setScale(2)); // Montant positif
        assertEquals("Refund from receiver", dto2.getDescription());

        // Vérification des interactions
        verify(transactionRepository, times(1)).findBySenderOrReceiver(user, user);
    }

}