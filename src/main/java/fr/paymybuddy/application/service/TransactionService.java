package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
    @RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;


    /**
     * Récupère toutes les transactions envoyées par un utilisateur spécifique.
     *
     * @param senderId l'identifiant de l'utilisateur expéditeur
     * @return une liste de transactions envoyées
     */
    public List<TransactionDTO> findTransactionsBySender(Long senderId) {
        // Récupérer la liste des transactions
        List<Transaction> transactions = transactionRepository.findBySender_Id(senderId);

        // Transformer chaque transaction en DTO
        return transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getReceiver().getUsername(),
                        transaction.getDescription(),
                        transaction.getAmount()
                ))
                .toList(); // Retourner une liste de DTOs
    }


    /**
     * Valide et initialise une transaction avant sauvegarde.
     *
     * @param sender   l'utilisateur expéditeur
     * @param receiver l'utilisateur destinataire
     * @param amount   le montant de la transaction
     * @param description la description facultative
     * @return la transaction sauvegardée
     */
    public Boolean createTransaction(User sender, Long receiver, double amount, String description) {
        // Validation business logic de base
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("La transaction nécessite un expéditeur et un destinataire.");
        }
        User userDestinataire = userService.getById(receiver);
        User userConecte = userService.getById(sender.getId());
        if (userDestinataire == null || userConecte == null) {
            throw new IllegalArgumentException("La transaction nécessite un expéditeur et un destinataire.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant de la transaction doit être supérieur à zéro.");
        }
        Transaction transaction = new Transaction();
        transaction.setSender(userConecte);
        transaction.setReceiver(userDestinataire);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        // Appeler la méthode de sauvegarde
        transactionRepository.save(transaction);
        return true;
    }
}
