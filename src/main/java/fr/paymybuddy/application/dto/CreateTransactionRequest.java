package fr.paymybuddy.application.dto;

import fr.paymybuddy.application.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
/**
 * DTO pour la création de transactions.
 */
@Getter
@Setter
@Data
public class CreateTransactionRequest {

    private User sender;
    private User receiver;
    private double amount;
    private String description;

}