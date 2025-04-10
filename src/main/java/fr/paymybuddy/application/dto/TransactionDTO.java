package fr.paymybuddy.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * DTO pour contenir uniquement les informations nécessaires dans une transaction :
 * le nom du destinataire, le montant et la description.
 */
@Getter
@Setter
@AllArgsConstructor
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
    private double amount;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDTO that = (TransactionDTO) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(receiverName, that.receiverName) && Objects.equals(description, that.description);
    }
}