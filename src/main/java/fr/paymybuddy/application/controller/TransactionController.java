package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.CreateTransactionRequest;
import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Endpoint pour créer une nouvelle transaction.
     *
     * @param transactionRequest Objet contenant les informations de transaction (corps de la requête).
     * @return La transaction créée.
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest transactionRequest) {
        Transaction transaction = transactionService.createTransaction(
                transactionRequest.getSender(),
                transactionRequest.getReceiver(),
                transactionRequest.getAmount(),
                transactionRequest.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    /**
     * Endpoint pour récupérer les transactions envoyées par un utilisateur.
     *
     * @param senderId L'ID de l'utilisateur expéditeur.
     * @return La liste des transactions envoyées.
     */
    @GetMapping("/sent")
    public ResponseEntity<List<Transaction>> getTransactionsBySender(@AuthenticationPrincipal User userConnecte) {
        List<Transaction> transactions = transactionService.findTransactionsBySender(userConnecte.getId());
        return ResponseEntity.ok(transactions);
    }

    /**
     * Endpoint pour récupérer les transactions reçues par un utilisateur.
     *
     * @param receiverId L'ID de l'utilisateur destinataire.
     * @return La liste des transactions reçues.
     */
    @GetMapping("/received/{receiverId}")
    public ResponseEntity<List<Transaction>> getTransactionsByReceiver(@PathVariable Long receiverId) {
        List<Transaction> transactions = transactionService.findTransactionsByReceiver(receiverId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Endpoint pour récupérer toutes les transactions impliquant un utilisateur.
     *
     * @param userId L'ID de l'utilisateur impliqué.
     * @return La liste des transactions où l'utilisateur est l'expéditeur ou le destinataire.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.findTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }
}