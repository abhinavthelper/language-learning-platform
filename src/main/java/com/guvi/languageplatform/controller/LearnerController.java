package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearnerController {

    @GetMapping("/learner/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("LEARNER")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "learner-dashboard";
    }
}