package fr.paymybuddy.application.service;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.repository.TransactionRepository;
import fr.paymybuddy.application.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j // lombok fournit automatiquement la logique pour logger avec SLF4J
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final UserRepository userRepository;


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
    public List<TransactionDTO> findMesTransactions(User user) {
        // Journalisation DEBUG pour début de traitement
        log.debug("Début de la recherche des transactions pour l'identifiant de l'expéditeur : {}", user.getId());

        // Récupérer la liste des transactions
        List<Transaction> transactions = transactionRepository.findBySenderOrReceiver(user,user);

        // Transformer chaque transaction en DTO
        return transactions.stream()
                .map(transaction -> {
                    // Vérifier si l'utilisateur est le sender ou le receiver
                    boolean isSender = transaction.getSender().getId().equals(user.getId());
                    boolean isReceiver = transaction.getReceiver().getId().equals(user.getId());

                    // Détermine le montant en fonction du rôle
                    BigDecimal amount = isSender
                            ? transaction.getAmount().negate() // Montant négatif si l'utilisateur est l'expéditeur
                            : transaction.getAmount(); // Montant positif si l'utilisateur est le destinataire

                    // Détermine le "receiver" dans le DTO
                    String receiverName = isReceiver
                            ? transaction.getSender().getUsername() // Si je suis le destinataire, on met le nom de l'expéditeur
                            : transaction.getReceiver().getUsername(); // Si je suis l'expéditeur, on met le nom du destinataire

                    // Créer et retourner le DTO
                    return new TransactionDTO(
                            receiverName, // Nom affiché
                            transaction.getDescription(),
                            amount
                    );
                })
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
    @Transactional
    public TransactionResult createTransaction(User sender, Long receiver, BigDecimal amount, String description) {
        // Validation business logic de base
        try {
            log.debug("Début de la création de la transaction pour l'expéditeur (senderId) : {}, destinataire (receiverId) : {}, montant : {}",
                    sender != null ? sender.getId() : "null", receiver, amount);

            if (sender == null || receiver == null) {
                String errorMessage = "La transaction nécessite un expéditeur et un destinataire.";
                log.error("Paramètres invalides : " + errorMessage);
                return new TransactionResult(false, errorMessage);
            }

            User userDestinataire = userService.getById(receiver);
            User userConecte = userService.getById(sender.getId());
            if (userDestinataire == null || userConecte == null) {
                String errorMessage = "L'un des utilisateurs spécifiés est introuvable dans la base de données.";
                log.error(errorMessage + " (senderId : {}, receiverId : {})", sender.getId(), receiver);
                return new TransactionResult(false, errorMessage);
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                String errorMessage = "Le montant de la transaction doit être supérieur à zéro.";
                log.error("Montant invalide pour la transaction : senderId = {}, receiverId = {}, montant = {}", sender.getId(), receiver, amount);
                return new TransactionResult(false, errorMessage);
            }

            if (amount.compareTo(userConecte.getSolde()) > 0) {
                String errorMessage = "Transaction impossible -  le solde disponible est de " + userConecte.getSolde().setScale(2, RoundingMode.HALF_UP) + " €.";
                log.error(errorMessage + " senderId = {}", sender.getId());
                return new TransactionResult(false, errorMessage);
            }

            // Soustraire du solde de l'expéditeur et sauvegarder
            userConecte.setSolde(userConecte.getSolde().subtract(amount));
            userRepository.save(userConecte);

            // Ajouter au solde du destinataire et sauvegarder
            userDestinataire.setSolde(userDestinataire.getSolde().add(amount));
            userRepository.save(userDestinataire);

            // Sauvegarder la transaction
            Transaction transaction = new Transaction();
            transaction.setSender(userConecte);
            transaction.setReceiver(userDestinataire);
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transactionRepository.save(transaction);

            // Succès
            log.info("Transaction créée avec succès : senderId = {}, receiverId = {}, montant = {}", sender.getId(), receiver, amount);
            return new TransactionResult(true, "La transaction a été créée avec succès.");
        } catch (Exception e) {
            String errorMessage = "Une erreur inattendue est survenue : " + e.getMessage();
            log.error(errorMessage, e);
            return new TransactionResult(false, errorMessage);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class TransactionResult {
        private final boolean success;
        private final String message;
    }

}
