package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les transactions.
 * Ce contrôleur expose des endpoints pour créer des transactions
 * et pour récupérer les transactions associées à un utilisateur.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    /**
     * Endpoint pour créer une nouvelle transaction.
     *
     * @param userConnecte L'utilisateur actuellement authentifié qui initie la transaction.
     * @param id_contact   L'identifiant du contact bénéficiaire de la transaction.
     * @param description  Une description ou un motif pour la transaction.
     * @param montant      Le montant de la transaction à transférer.
     * @return Un boolean indiquant si la transaction a été effectuée avec succès.
     */
    @PostMapping
    public ResponseEntity<Boolean> createTransaction(
            @AuthenticationPrincipal User userConnecte,
            @RequestParam Long id_contact,
            @RequestParam String description,
            @RequestParam double montant
    ) {
        logger.debug("Début du traitement de la transaction pour l'utilisateur : {}", userConnecte.getUsername());
        try {
            boolean result = transactionService.createTransaction(
                    userConnecte,
                    id_contact,
                    montant,
                    description
            );
            logger.info("Transaction créée avec succès pour l'utilisateur : {}", userConnecte.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la création de la transaction pour l'utilisateur : {}", userConnecte.getUsername(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Endpoint pour récupérer les transactions envoyées par un utilisateur.
     *
     * @param userConnecte L'utilisateur actuellement authentifié dont les transactions envoyées
     *                     seront récupérées.
     * @return La liste des transactions envoyées par l'utilisateur sous forme de DTO.
     */
    @GetMapping("/sent")
    public ResponseEntity<List<TransactionDTO>> getTransactionsBySender(@AuthenticationPrincipal User userConnecte) {
        logger.debug("Récupération des transactions envoyées par l'utilisateur : {}", userConnecte.getUsername());
        List<TransactionDTO> transactions = transactionService.findTransactionsBySender(userConnecte.getId());
        logger.info("Récupération réussie de {} transactions pour l'utilisateur : {}", transactions.size(), userConnecte.getUsername());
        return ResponseEntity.ok(transactions);
    }




}