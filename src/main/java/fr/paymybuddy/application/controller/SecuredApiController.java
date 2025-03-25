package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecuredApiController {


    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(user.getEmail()); // Retourne l'utilisateur connecté
    }
}