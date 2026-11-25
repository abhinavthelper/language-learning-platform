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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/** Admin can edit a user's name, role and password. Email stays fixed because it links the tables. */
@Controller
public class AdminUserEditController {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "INSTRUCTOR", "LEARNER");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public AdminUserEditController(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/admin/users/{id}/edit")
    public String form(@PathVariable Long id, Model model) {
        User target = userRepository.findById(id).orElse(null);
        if (target == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("target", target);
        return "admin-user-edit";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String update(@PathVariable Long id, @RequestParam String name, @RequestParam String role,
                         @RequestParam(required = false) String password,
                         HttpSession session, Authentication authentication, RedirectAttributes redirect) {
        User me = currentUserService.getCurrentUser(session, authentication);
        User target = userRepository.findById(id).orElse(null);
        if (me == null || target == null) {
            return "redirect:/admin/users";
        }

        String cleanName = name == null ? "" : name.trim();
        String cleanRole = role == null ? "" : role.trim().toUpperCase();

        if (cleanName.isEmpty() || cleanName.length() > 100) {
            redirect.addFlashAttribute("error", "Name must be between 1 and 100 characters.");
            return "redirect:/admin/users";
        }
        if (!ALLOWED_ROLES.contains(cleanRole)) {
            redirect.addFlashAttribute("error", "Invalid role selected.");
            return "redirect:/admin/users";
        }
        if (me.getEmail().equals(target.getEmail()) && !"ADMIN".equals(cleanRole)) {
            redirect.addFlashAttribute("error", "You cannot change your own role.");
            return "redirect:/admin/users";
        }
        if (password != null && !password.trim().isEmpty()) {
            if (password.trim().length() < 6) {
                redirect.addFlashAttribute("error", "Password must be at least 6 characters.");
                return "redirect:/admin/users";
            }
            target.setPassword(passwordEncoder.encode(password));
        }

        target.setName(cleanName);
        target.setRole(cleanRole);
        userRepository.save(target);
        redirect.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
    }
}
