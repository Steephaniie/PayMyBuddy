package fr.paymybuddy.application.repository;

import fr.paymybuddy.application.model.Transaction;
import fr.paymybuddy.application.model.User;
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
     * Récupère une liste de transactions pour lesquelles l'utilisateur spécifié
     * est soit l'émetteur soit le récepteur.
     *
     * @param user le premier utilisateur impliqué dans la transaction,
     *             qui peut être l'émetteur ou le récepteur
     * @param user1 le deuxième utilisateur impliqué dans la transaction,
     *              qui peut être l'émetteur ou le récepteur
     * @return une liste d'objets Transaction correspondant aux critères spécifiés
     */
    List<Transaction> findBySenderOrReceiver(User user, User user1);
}