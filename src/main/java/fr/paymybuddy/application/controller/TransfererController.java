package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.dto.TransactionDTO;
import fr.paymybuddy.application.dto.UserDTO;
import fr.paymybuddy.application.model.User;
import fr.paymybuddy.application.service.TransactionService;
import fr.paymybuddy.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransfererController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/transferer")
    public String viewTransactionsBySender(@AuthenticationPrincipal User userConnecte, Model model) {
        logger.debug("Récupération des transactions pour la vue Thymeleaf de l'utilisateur : {}", userConnecte.getUsername());
        try {
            List<TransactionDTO> transactions = transactionService.findTransactionsBySender(userConnecte.getId());
            List<UserDTO> contacts = userService.getContacts(userConnecte); // Récupération des contacts
            model.addAttribute("contacts", contacts); // Ajout des contacts au modèle

            model.addAttribute("transactions", transactions);
            model.addAttribute("username", userConnecte.getUsername()); // Optionnel pour afficher le nom de l'utilisateur
            return "Transferer"; // Le nom de la page Thymeleaf à afficher (transactions.html)
        } catch (Exception e) {
            logger.error("Impossible de récupérer les transactions pour le user : {}", userConnecte.getUsername(), e);
            return "error"; // Redirection vers une page d'erreur si nécessaire
        }
    }
    @PostMapping("/payer")
    public String payer(@AuthenticationPrincipal User userConnecte, Model model,
        @RequestParam Long id_contact,
        @RequestParam String description,
        @RequestParam double montant
    ) {
            logger.debug("Début du traitement de la transaction pour l'utilisateur : {}", userConnecte.getUsername());
            try {
              transactionService.createTransaction(
                        userConnecte,
                        id_contact,
                        montant,
                        description
                );
                logger.info("Transaction créée avec succès pour l'utilisateur : {}", userConnecte.getUsername());
               return viewTransactionsBySender(userConnecte, model);
            } catch (Exception e) {
                logger.error("Une erreur est survenue lors de la création de la transaction pour l'utilisateur : {}", userConnecte.getUsername(), e);
                return "error"; // Redirection vers une page d'erreur si nécessaire
            }
    }
}
