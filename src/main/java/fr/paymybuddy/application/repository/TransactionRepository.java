package fr.paymybuddy.application.repository;

import fr.paymybuddy.application.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Récupère toutes les transactions envoyées par un utilisateur spécifique.
     *
     * @param senderId l'identifiant de l'utilisateur expéditeur
     * @return une liste de transactions envoyées par l'utilisateur
     */
    List<Transaction> findBySender_Id(Long senderId);

    /**
     * Récupère toutes les transactions reçues par un utilisateur spécifique.
     *
     * @param receiverId l'identifiant de l'utilisateur destinataire
     * @return une liste de transactions reçues par l'utilisateur
     */
    List<Transaction> findByReceiver_Id(Long receiverId);

    /**
     * Récupère toutes les transactions où l'utilisateur donné est soit l'expéditeur soit le destinataire.
     *
     * @param userId1 l'identifiant de l'utilisateur concerné
     * @return une liste de transactions impliquant l'utilisateur
     */
    List<Transaction> findBySender_IdOrReceiver_Id(Long userId1, Long userId2);
}