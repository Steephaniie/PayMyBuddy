package fr.paymybuddy.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente une transaction monétaire entre deux utilisateurs.
 */
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    /**
     * Identifiant unique de la transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    /**
     * L'utilisateur qui envoie les fonds dans la transaction.
     * <p>Relation ManyToOne avec la classe User.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Modifier selon les besoins
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * L'utilisateur qui reçoit les fonds dans la transaction.
     * <p>Relation ManyToOne avec la classe User.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Modifier selon les besoins
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Montant de la transaction.
     */
    @Column(nullable = false)
    private double amount;

    /**
     * Description facultative pour la transaction (par exemple : "Remboursement dîner").
     */
    @Column(length = 255, nullable = true) // Nullable autorisé si la description est optionnelle
    private String description;

}
