package com.guvi.languageplatform.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session, Model model) {
        Object loginError = session.getAttribute("loginError");
        if (loginError != null) {
            model.addAttribute("error", loginError.toString());
            session.removeAttribute("loginError");
        }
        return "login";
    }
}
