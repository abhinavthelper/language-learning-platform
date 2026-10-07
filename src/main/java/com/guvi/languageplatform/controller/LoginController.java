package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("email") String email,
                          @RequestParam("password") String password,
                          @RequestParam(value = "role", required = false) String role,
                          HttpSession session, Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        if (role != null && !role.isEmpty() && !user.getRole().equals(role)) {
            model.addAttribute("error", "This is not a " + role.toLowerCase() + " account. Pick the correct tab.");
            return "login";
        }

        session.setAttribute("loggedUser", user);

        if (user.getRole().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole().equals("INSTRUCTOR")) {
            return "redirect:/instructor/dashboard";
        } else {
            return "redirect:/learner/dashboard";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}