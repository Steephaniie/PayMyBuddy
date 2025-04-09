package fr.paymybuddy.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * DTO pour transférer les informations utilisateur essentielles.
 * <p>
 * Contient les informations nécessaires à propos d'un utilisateur, telles que :
 * - Nom d'utilisateur (username)
 * - Adresse e-mail (email)
 * - Identifiant unique (id)
 */
@Getter
@AllArgsConstructor
public class UserDTO {
    private static final Logger logger = LoggerFactory.getLogger(UserDTO.class);

    /**
     * Nom d'utilisateur de l'utilisateur.
     */
    private String username;

    static {
        logger.info("La classe UserDTO a été chargée avec succès.");
    }

    /**
     * Adresse e-mail de l'utilisateur.
     */
    private String email;

    /**
     * Identifiant unique de l'utilisateur.
     */
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(username, userDTO.username) &&
                Objects.equals(email, userDTO.email);
    }

}