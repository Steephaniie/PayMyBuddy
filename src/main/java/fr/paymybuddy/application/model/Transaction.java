package fr.paymybuddy.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;


import java.math.BigDecimal;

/**
 * Représente une transaction monétaire entre deux utilisateurs.
 * <p>
 * Cette classe gère les informations nécessaires à une transaction,
 * dont l'identifiant unique, les utilisateurs impliqués (émetteur et récepteur),
 * ainsi que les détails du montant et une description facultative.
 * Elle est mappée à la table 'transactions' en base de données.
 * </p>
 *
 * @author
 * @see fr.paymybuddy.application.model.User
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    /**
     * Logger pour journaliser les événements liés aux transactions.
     */
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    /**
     * Identifiant unique de la transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    /**
     * L'utilisateur qui envoie les fonds dans la transaction.
     * <p>
     * Relation ManyToOne avec la classe {@link User}. Chaque transaction est liée
     * à un émetteur unique, référencé via une clé étrangère 'sender_id'.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Modifier selon les besoins
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * L'utilisateur qui reçoit les fonds dans la transaction.
     * <p>
     * Relation ManyToOne avec la classe {@link User}. Chaque transaction est liée
     * à un récepteur unique, référencé via une clé étrangère 'receiver_id'.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Modifier selon les besoins
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Montant de la transaction.
     * <p>
     * Cet attribut représente la somme transférée dans la transaction,
     * exprimée en devise monétaire standard (par exemple : euros).
     * </p>
     */
    @Column(nullable = false)
    @DecimalMin("0.00")
    @DecimalMax("9999999.99")
    @Digits(integer=7, fraction=2)

    private BigDecimal amount;

    /**
     * Description facultative pour la transaction.
     * <p>
     * Ce champ permet d'inclure des informations supplémentaires sur la nature
     * ou le but de la transaction (par exemple : "Remboursement dîner").
     * </p>
     */
    @Column(length = 255, nullable = true) // Nullable autorisé si la description est optionnelle
    private String description;

}
