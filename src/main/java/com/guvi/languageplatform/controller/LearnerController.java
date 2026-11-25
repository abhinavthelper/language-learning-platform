package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearnerController {

    private final CurrentUserService currentUserService;

    public LearnerController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/learner/dashboard")
    public String dashboard(HttpSession session, Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null || !"LEARNER".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "learner-dashboard";
    }
}
