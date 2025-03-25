package fr.paymybuddy.application.controller;

import fr.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Retourne le nom du fichier HTML (sans .html)
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model) {
        try {
            userService.registerUser(username, password,email);
            return "redirect:/login"; // Redirige vers la page de login après succès
        } catch (Exception e) {
            //TODO faire un message d'erreur adapté
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            return "register"; // Retourne à la page d'inscription avec un message d'erreur
        }
    }
}