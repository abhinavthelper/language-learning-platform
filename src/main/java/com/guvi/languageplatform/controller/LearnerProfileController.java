package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public LearnerProfileController(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/learner/profile")
    public String profile(HttpSession session, Authentication authentication, Model model) {
        User sessionUser = currentUserService.getCurrentUser(session, authentication);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("profile", userRepository.findByEmail(sessionUser.getEmail()));
        return "learner-profile";
    }

    @PostMapping("/learner/profile")
    public String update(@RequestParam String name,
                         @RequestParam(required = false) String learningPreference,
                         @RequestParam(required = false) String password,
                         HttpSession session, Authentication authentication, RedirectAttributes redirect) {
        User sessionUser = currentUserService.getCurrentUser(session, authentication);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(sessionUser.getEmail());
        if (user == null) {
            return "redirect:/login";
        }
        if (InputValidator.isBlankOrTooLong(name, 100)) {
            redirect.addFlashAttribute("error", "Name must be between 1 and 100 characters.");
            return "redirect:/learner/profile";
        }
        if (learningPreference != null && learningPreference.trim().length() > 255) {
            redirect.addFlashAttribute("error", "Learning preference must be 255 characters or fewer.");
            return "redirect:/learner/profile";
        }
        if (password != null && !password.trim().isEmpty()) {
            if (password.trim().length() < 6) {
                redirect.addFlashAttribute("error", "Password must be at least 6 characters.");
                return "redirect:/learner/profile";
            }
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setName(name.trim());
        user.setLearningPreference(learningPreference == null ? null : learningPreference.trim());
        userRepository.save(user);
        session.setAttribute("loggedUser", user);
        redirect.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/learner/profile";
    }
}
