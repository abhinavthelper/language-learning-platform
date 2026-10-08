package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin can edit a user's name, role and password. Email stays fixed because it links the tables. */
@Controller
public class AdminUserEditController {

    private final UserRepository userRepository;

    public AdminUserEditController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users/{id}/edit")
    public String form(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
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
                         HttpSession session, RedirectAttributes redirect) {
        User me = (User) session.getAttribute("loggedUser");
        User target = userRepository.findById(id).orElse(null);
        if (me == null || target == null) {
            return "redirect:/admin/users";
        }
        // An admin must not remove their own admin role
        if (me.getEmail().equals(target.getEmail()) && !"ADMIN".equals(role)) {
            redirect.addFlashAttribute("error", "You cannot change your own role.");
            return "redirect:/admin/users";
        }
        target.setName(name.trim());
        target.setRole(role);
        if (password != null && !password.trim().isEmpty()) {
            target.setPassword(password);
        }
        userRepository.save(target);
        redirect.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
    }
}
