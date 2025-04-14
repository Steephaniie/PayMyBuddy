package fr.paymybuddy.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * DTO pour contenir uniquement les informations nécessaires dans une transaction :
 * le nom du destinataire, le montant et la description.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class TransactionDTO {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDTO.class);

    /**
     * Le nom du destinataire de la transaction.
     */
    private String receiverName;

    /**
     * Une description facultative de la transaction.
     */
    private String description;

    /**
     * Le montant associé à la transaction.
     */
    @DecimalMin("0.00")
    @DecimalMax("9999999.99")
    @Digits(integer=7, fraction=2)
    private BigDecimal amount;

}