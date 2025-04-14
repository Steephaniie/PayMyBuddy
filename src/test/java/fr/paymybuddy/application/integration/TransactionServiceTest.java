package fr.paymybuddy.application.integration;

import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.TransactionRepository;
import fr.paymybuddy.application.repository.UserRepository;
import fr.paymybuddy.application.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @Transactional // Cette annotation garantit le rollback après le test
    void testCreateTransaction_avecRollback() {
        // Arrange : Ajouter des utilisateurs dans la base de données
        User sender = new User();
        sender.setEmail("expediteur@test.com");
        sender.setSolde(BigDecimal.valueOf(100));
        userRepository.save(sender);

        User receiver = new User();
        receiver.setEmail("destinataire@test.com");
        receiver.setSolde(BigDecimal.valueOf(50));
        userRepository.save(receiver);
        int nombreTransactionAvant = transactionRepository.findAll().size();

        // Act : Tenter de créer une transaction
        TransactionService.TransactionResult result = transactionService.createTransaction(
                sender, 
                receiver.getId(), 
                BigDecimal.valueOf(30), 
                "Paiement test"
        );

        // Assert : Vérifier que la transaction a été correctement exécutée
        assertTrue(result.isSuccess());
        assertEquals("La transaction a été créée avec succès.", result.getMessage());

        // Vérifier que les soldes ont été mis à jour temporairement
        User actualSender = userRepository.findById(sender.getId()).orElseThrow();
        User actualReceiver = userRepository.findById(receiver.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(70), actualSender.getSolde());
        assertEquals(BigDecimal.valueOf(80), actualReceiver.getSolde());

        // Transaction enregistrée temporairement
        assertEquals(nombreTransactionAvant+1, transactionRepository.findAll().size());

        // Une fois le test terminé, les données sont roll-backées automatiquement
    }
}