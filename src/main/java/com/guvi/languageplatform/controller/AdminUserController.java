package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.regex.Pattern;

/** Admin can create and delete user accounts. */
@Controller
public class AdminUserController {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "INSTRUCTOR", "LEARNER");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public AdminUserController(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/admin/users/new")
    public String form() {
        return "admin-user-form";
    }

    @PostMapping("/admin/users/create")
    public String create(@RequestParam String name, @RequestParam String email,
                         @RequestParam String password, @RequestParam String role,
                         RedirectAttributes redirect) {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim();
        String cleanRole = role == null ? "" : role.trim().toUpperCase();

        if (cleanName.isEmpty() || cleanName.length() > 100) {
            redirect.addFlashAttribute("error", "Name must be between 1 and 100 characters.");
            return "redirect:/admin/users";
        }
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches() || cleanEmail.length() > 150) {
            redirect.addFlashAttribute("error", "Please enter a valid email address.");
            return "redirect:/admin/users";
        }
        if (password == null || password.trim().length() < 6) {
            redirect.addFlashAttribute("error", "Password must be at least 6 characters.");
            return "redirect:/admin/users";
        }
        if (!ALLOWED_ROLES.contains(cleanRole)) {
            redirect.addFlashAttribute("error", "Invalid role selected.");
            return "redirect:/admin/users";
        }
        if (userRepository.findByEmail(cleanEmail) != null) {
            redirect.addFlashAttribute("error", "A user with this email already exists.");
            return "redirect:/admin/users";
        }

        userRepository.save(new User(cleanName, cleanEmail, passwordEncoder.encode(password), cleanRole));
        redirect.addFlashAttribute("success", "User created successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,
                         Authentication authentication, RedirectAttributes redirect) {
        User me = currentUserService.getCurrentUser(session, authentication);
        User target = userRepository.findById(id).orElse(null);
        if (target == null) {
            redirect.addFlashAttribute("error", "User not found.");
        } else if (me != null && me.getEmail().equals(target.getEmail())) {
            redirect.addFlashAttribute("error", "You cannot delete your own account.");
        } else {
            userRepository.deleteById(id);
            redirect.addFlashAttribute("success", "User deleted successfully.");
        }
        return "redirect:/admin/users";
    }
}
