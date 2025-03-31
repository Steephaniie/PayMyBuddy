package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.TransactionDTO;
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
     * @return La transaction créée.
     */
    @PostMapping
    public ResponseEntity<Boolean> createTransaction(
            @AuthenticationPrincipal User userConnecte,
            @RequestParam Long id_contact,
            @RequestParam String description,
            @RequestParam double montant
            ) {
        return ResponseEntity.ok(
                transactionService.createTransaction(
                userConnecte,
                id_contact,
                montant,
                description
        ));
    }

    /**
     * Endpoint pour récupérer les transactions envoyées par un utilisateur.
     *
     * @return La liste des transactions envoyées.
     */
    @GetMapping("/sent")
    public ResponseEntity<List<TransactionDTO>> getTransactionsBySender(@AuthenticationPrincipal User userConnecte) {
        List<TransactionDTO> transactions = transactionService.findTransactionsBySender(userConnecte.getId());
        return ResponseEntity.ok(transactions);
    }



}