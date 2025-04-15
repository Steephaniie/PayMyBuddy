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

/**
 * Classe d'intégration pour tester les fonctionnalités de TransactionService.
 * Utilise une transaction pour s'assurer que les données effectuent un rollback après chaque test,
 * garantissant ainsi l'isolation des tests.
 */
@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Teste la création d'une transaction entre deux utilisateurs avec un rollback automatique.
     * Ce test permet de valider :
     * - Qu'une transaction valide est bien effectuée.
     * - Que les soldes des utilisateurs sont temporairement mis à jour.
     * - Que les modifications effectuées par le test sont annulées après l'exécution.
     */
    @Test
    @Transactional
    // Cette annotation garantit un rollback automatique à la fin du test
    void testCreateTransaction_avecRollback() {
        // Préparation : Ajouter deux utilisateurs dans la base de données avec des soldes initiaux
        User sender = new User();
        sender.setEmail("expediteur@test.com");
        sender.setSolde(BigDecimal.valueOf(100));
        userRepository.save(sender);

        User receiver = new User();
        receiver.setEmail("destinataire@test.com");
        receiver.setSolde(BigDecimal.valueOf(50));
        userRepository.save(receiver);
        int nombreTransactionAvant = transactionRepository.findAll().size();

        // Action : Effectuer une transaction de 30 unités du compte de l'expéditeur vers celui du destinataire
        TransactionService.TransactionResult result = transactionService.createTransaction(
                sender, 
                receiver.getId(), 
                BigDecimal.valueOf(30), 
                "Paiement test"
        );

        // Validation : Vérifier que la transaction a été réalisée avec succès
        assertTrue(result.isSuccess());
        assertEquals("La transaction a été créée avec succès.", result.getMessage());

        // Vérification : Confirmer que les soldes des deux utilisateurs sont mis à jour
        // Note : Ces modifications seront annulées après le rollback
        User actualSender = userRepository.findById(sender.getId()).orElseThrow();
        User actualReceiver = userRepository.findById(receiver.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(70), actualSender.getSolde());
        assertEquals(BigDecimal.valueOf(80), actualReceiver.getSolde());

        // Vérification supplémentaire : Confirmer que la transaction est bien enregistrée dans la base temporairement
        assertEquals(nombreTransactionAvant+1, transactionRepository.findAll().size());

        // Une fois le test terminé, les données sont roll-backées automatiquement
    }
}