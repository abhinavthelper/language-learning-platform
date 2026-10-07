package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin can create and delete user accounts. */
@Controller
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users/new")
    public String form(HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "admin-user-form";
    }

    @PostMapping("/admin/users/create")
    public String create(@RequestParam String name, @RequestParam String email,
                         @RequestParam String password, @RequestParam String role,
                         RedirectAttributes redirect) {
        if (userRepository.findByEmail(email) != null) {
            redirect.addFlashAttribute("error", "A user with this email already exists.");
        } else {
            userRepository.save(new User(name.trim(), email.trim(), password, role));
            redirect.addFlashAttribute("success", "User created successfully.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User me = (User) session.getAttribute("loggedUser");
        User target = userRepository.findById(id).orElse(null);
        if (target == null) {
            redirect.addFlashAttribute("error", "User not found.");
        } else if (me != null && me.getEmail().equals(target.getEmail())) {
            // An admin must not delete their own account
            redirect.addFlashAttribute("error", "You cannot delete your own account.");
        } else {
            userRepository.deleteById(id);
            redirect.addFlashAttribute("success", "User deleted successfully.");
        }
        return "redirect:/admin/users";
    }
}
