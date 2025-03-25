package fr.paymybuddy.application.service;

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

    /**
     * Sauvegarde une nouvelle transaction dans la base de données.
     *
     * @param transaction l'objet transaction à sauvegarder
     * @return la transaction sauvegardée
     */
    public Transaction saveTransaction(Transaction transaction) {
        // Logique supplémentaire éventuelle avant enregistrement
        return transactionRepository.save(transaction);
    }

    /**
     * Récupère toutes les transactions envoyées par un utilisateur spécifique.
     *
     * @param senderId l'identifiant de l'utilisateur expéditeur
     * @return une liste de transactions envoyées
     */
    public List<Transaction> findTransactionsBySender(Long senderId) {
        return transactionRepository.findBySender_Id(senderId);
    }

    /**
     * Récupère toutes les transactions reçues par un utilisateur spécifique.
     *
     * @param receiverId l'identifiant de l'utilisateur destinataire
     * @return une liste de transactions reçues
     */
    public List<Transaction> findTransactionsByReceiver(Long receiverId) {
        return transactionRepository.findByReceiver_Id(receiverId);
    }

    /**
     * Récupère toutes les transactions où un utilisateur est impliqué (comme expéditeur ou destinataire).
     *
     * @param userId l'identifiant de l'utilisateur concerné
     * @return une liste de transactions correspondant
     */
    public List<Transaction> findTransactionsByUser(Long userId) {
        return transactionRepository.findBySender_IdOrReceiver_Id(userId, userId);
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
    public Transaction createTransaction(User sender, User receiver, double amount, String description) {
        // Validation business logic de base
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("La transaction nécessite un expéditeur et un destinataire.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant de la transaction doit être supérieur à zéro.");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        // Appeler la méthode de sauvegarde
        return saveTransaction(transaction);
    }
}
