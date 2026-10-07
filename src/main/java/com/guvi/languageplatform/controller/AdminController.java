package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public AdminController(UserRepository userRepository, LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String users(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/admin/content")
    public String content(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        model.addAttribute("lessons", lessonRepository.findAll());
        return "admin-content";
    }

    @PostMapping("/admin/content/approve/{id}")
    public String approveContent(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson != null) {
            lesson.setStatus("APPROVED");
            lessonRepository.save(lesson);
        }

        return "redirect:/admin/content";
    }

    @GetMapping("/admin/settings")
    public String settings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        return "admin-settings";
    }
}