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

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur responsable de la gestion des transactions entre utilisateurs.
 * Ce contrôleur gère les fonctionnalités de visualisation des transactions,
 * d'accès à l'accueil et de création de nouvelles transactions via une interface utilisateur.
 */
@Controller
@RequiredArgsConstructor
public class TransfererController {
    private static final Logger logger = LoggerFactory.getLogger(TransfererController.class);

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Redirige l'utilisateur connecté vers la page d'accueil.
     * L'accueil affiche les transactions en cours et les informations de solde.
     *
     * @param userConnecte L'utilisateur actuellement connecté.
     * @param model        Modèle utilisé pour transmettre des données à la vue.
     * @return Le nom de la vue correspondante à afficher.
     */
    @GetMapping("/")
    public String goHome(@AuthenticationPrincipal User userConnecte, Model model) {
        return viewTransactionsBySender(userConnecte, model);
    }

    /**
     * Affiche les transactions pour l'utilisateur connecté ainsi que ses contacts.
     * Prépare les données nécessaires (transactions, contacts, solde) pour l'intégration dans la vue Thymeleaf.
     *
     * @param userConnecte L'utilisateur actuellement connecté.
     * @param model        Modèle contenant les données à afficher dans la vue.
     * @return Le nom de la vue correspondant à la page des transferts d'argent.
     */
    @GetMapping("/transferer")
    public String viewTransactionsBySender(@AuthenticationPrincipal User userConnecte, Model model) {
        logger.debug("Récupération des transactions pour la vue Thymeleaf de l'utilisateur : {}", userConnecte.getUsername());
        try {
            // Récupération des transactions de l'utilisateur connecté
            List<TransactionDTO> transactions = transactionService.findMesTransactions(userConnecte);

            // Récupération de la liste des contacts de l'utilisateur
            List<UserDTO> contacts = userService.getContacts(userConnecte);

            // Récupération des informations de l'utilisateur connecté, y compris le solde
            User user = userService.getById(userConnecte.getId());
            model.addAttribute("solde", user.getUsername() + " solde=" + user.getSolde() + "€"); // Affichage du solde
            model.addAttribute("contacts", contacts); // Ajout des contacts pour affichage
            model.addAttribute("transactions", transactions); // Ajout des transactions
            model.addAttribute("username", userConnecte.getUsername()); // Nom de l'utilisateur pour affichage

            return "Transferer"; // Nom de la page vue pour la liste des transferts
        } catch (Exception e) {
            logger.error("Impossible de récupérer les transactions pour le user : {}", userConnecte.getUsername(), e);
            return "error"; // Redirection vers une page d'erreur si une exception est levée
        }
    }

    /**
     * Effectue une transaction entre l'utilisateur connecté et un contact spécifié.
     * Valide les informations de la transaction, crée l'opération et ajoute
     * un message au modèle indiquant le succès ou l'échec de l'opération.
     *
     * @param userConnecte L'utilisateur actuellement connecté initiant la transaction.
     * @param model        Modèle pour transmettre des données à la vue.
     * @param id_contact   Identifiant du contact qui recevra la transaction.
     * @param description  Description ou motif de la transaction.
     * @param montant      Montant de la transaction en euros.
     * @return La même page de vue avec un message mis à jour.
     */
    @PostMapping("/payer")
    public String payer(@AuthenticationPrincipal User userConnecte, Model model,
                        @RequestParam Long id_contact,
                        @RequestParam String description,
                        @RequestParam BigDecimal montant
    ) {
        logger.debug("Début du traitement de la transaction pour l'utilisateur : {}", userConnecte.getUsername());

        // Crée une nouvelle transaction via le service dédié
        TransactionService.TransactionResult retour = transactionService.createTransaction(
                userConnecte,
                id_contact,
                montant,
                description
        );

        // Gère le résultat de l'opération en affichant un message adéquat
        if (retour.isSuccess()) {
            model.addAttribute("message", "Transaction réalisée avec succès");
            logger.info("Transaction créée avec succès pour l'utilisateur : {}", userConnecte.getUsername());
        } else {
            model.addAttribute("message", retour.getMessage());
            logger.error("Une erreur est survenue lors de la création de la transaction pour l'utilisateur : {} {}",
                    userConnecte.getUsername(), retour.getMessage());
        }

        return viewTransactionsBySender(userConnecte, model); // Recharge la vue avec les mises à jour
    }
}
