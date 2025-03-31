package fr.paymybuddy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour contenir uniquement les informations nécessaires dans une transaction : 
 * le nom du destinataire, le montant et la description.
 */
@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {
    private String receiverName;
    private String description;
    private double amount;
}