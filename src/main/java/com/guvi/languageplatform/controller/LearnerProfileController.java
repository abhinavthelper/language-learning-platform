package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Lets a learner view and update their own profile. Email is the key, so it is read-only. */
@Controller
public class LearnerProfileController {

    private final UserRepository userRepository;

    public LearnerProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/learner/profile")
    public String profile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("loggedUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        // Reload from the database so the form always shows saved values
        model.addAttribute("profile", userRepository.findByEmail(sessionUser.getEmail()));
        return "learner-profile";
    }

    @PostMapping("/learner/profile")
    public String update(@RequestParam String name,
                         @RequestParam(required = false) String learningPreference,
                         @RequestParam(required = false) String password,
                         HttpSession session, RedirectAttributes redirect) {
        User sessionUser = (User) session.getAttribute("loggedUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(sessionUser.getEmail());
        if (name.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "Name cannot be empty.");
            return "redirect:/learner/profile";
        }
        user.setName(name.trim());
        user.setLearningPreference(learningPreference);
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
        }
        userRepository.save(user);
        session.setAttribute("loggedUser", user); // keep the session in sync
        redirect.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/learner/profile";
    }
}