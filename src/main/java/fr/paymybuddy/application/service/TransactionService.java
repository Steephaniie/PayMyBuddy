package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j // lombok fournit automatiquement la logique pour logger avec SLF4J
    @RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;


    /**
     * Récupère toutes les transactions envoyées par un utilisateur spécifique
     * et les convertit en objets TransactionDTO pour une utilisation optimisée dans les vues ou l'API.
     *
     * @param senderId l'identifiant unique de l'utilisateur expéditeur
     * @return une liste de TransactionDTO représentant les transactions envoyées
     */
    public List<TransactionDTO> findTransactionsBySender(Long senderId) {
        // Journalisation DEBUG pour début de traitement
        log.debug("Début de la recherche des transactions pour l'identifiant de l'expéditeur (senderId) : {}", senderId);

        // Récupérer la liste des transactions
        List<Transaction> transactions = transactionRepository.findBySender_Id(senderId);

        // Journalisation INFO pour action réussie
        log.info("Transactions récupérées avec succès pour l'identifiant de l'expéditeur (senderId) : {}", senderId);

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
     * Valide les données et crée une nouvelle transaction entre un expéditeur et un destinataire.
     * Cette méthode vérifie plusieurs conditions comme la présence des utilisateurs,
     * un montant valide et initialise ensuite l'objet Transaction avant de le sauvegarder.
     *
     * @param sender      l'utilisateur qui initie la transaction (expéditeur)
     * @param receiver    l'identifiant de l'utilisateur qui reçoit la transaction (destinataire)
     * @param amount      le montant de la transaction, qui doit être strictement supérieur à zéro
     * @param description une description facultative de la transaction (par exemple, motif du paiement)
     * @return true si la transaction est créée et sauvegardée avec succès
     * @throws IllegalArgumentException si les utilisateurs ou le montant sont invalides
     */
    public Boolean createTransaction(User sender, Long receiver, double amount, String description) {
        // Validation business logic de base
        try {
            // Journalisation DEBUG pour début de traitement
            log.debug("Début de la création de la transaction pour l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}", sender.getId(), receiver, amount);

            if (sender == null || receiver == null) {
                log.error("L'expéditeur (senderId : {}) ou le destinataire (receiverId : {}) est nul.", sender != null ? sender.getId() : "null", receiver);
                throw new IllegalArgumentException("La transaction nécessite un expéditeur et un destinataire.");
            }

            log.debug("Récupération de l'utilisateur destinataire avec receiverId: {}", receiver);
            User userDestinataire = userService.getById(receiver);
            log.debug("Récupération de l'utilisateur connecté avec senderId: {}", sender.getId());
            User userConecte = userService.getById(sender.getId());
            if (userDestinataire == null || userConecte == null) {
                log.error("L'expéditeur (senderId : {}) ou le destinataire (receiverId : {}) est introuvable dans la base de données.", sender.getId(), receiver);
                throw new IllegalArgumentException("La transaction nécessite un expéditeur et un destinataire.");
            }

            if (amount <= 0) {
                log.error("Montant invalide détecté pour la transaction avec l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}", sender.getId(), receiver, amount);
                throw new IllegalArgumentException("Le montant de la transaction doit être supérieur à zéro.");
            }

            // Journalisation DEBUG pour initialisation de la transaction
            log.debug("Initialisation de l'objet de la transaction pour l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}", sender.getId(), receiver, amount);
            Transaction transaction = new Transaction();
            log.debug("Assignation de l'expéditeur (sender) à la transaction : {}", userConecte.getId());
            transaction.setSender(userConecte);
            log.debug("Assignation du destinataire (receiver) à la transaction : {}", userDestinataire.getId());
            transaction.setReceiver(userDestinataire);
            log.debug("Assignation du montant (amount) à la transaction : {}", amount);
            transaction.setAmount(amount);
            log.debug("Assignation de la description à la transaction : {}", description);
            transaction.setDescription(description);
            log.info("L'objet de la transaction a été initialisé avec succès pour l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}", sender.getId(), receiver, amount);

            // Appeler la méthode de sauvegarde
            log.debug("Sauvegarde de la transaction dans la base de données...");
            transactionRepository.save(transaction);

            // Journalisation INFO pour succès
            log.info("Transaction créée avec succès pour l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}", sender.getId(), receiver, amount);

            return true;

        } catch (IllegalArgumentException e) {
            // Journalisation ERROR en cas d'erreur
            log.error("Échec de la création de la transaction : {}", e.getMessage());
            throw e;
        }
    }
}
