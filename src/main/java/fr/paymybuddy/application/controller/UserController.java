package fr.paymybuddy.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 * Permet de créer, lire, mettre à jour et supprimer des utilisateurs via des requêtes HTTP.
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;









}