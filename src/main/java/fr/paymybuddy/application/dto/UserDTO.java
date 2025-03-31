package fr.paymybuddy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO pour transférer les informations utilisateur essentielles.
 */
@Getter
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String email;
    private Long id;
}