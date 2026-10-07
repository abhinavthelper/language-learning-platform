package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String users(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }
}