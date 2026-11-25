package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.UserRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CurrentUserService currentUserService;

    public AdminController(UserRepository userRepository,
                           LessonRepository lessonRepository,
                           CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/admin/content")
    public String content(Model model) {
        model.addAttribute("lessons", lessonRepository.findAll());
        return "admin-content";
    }

    @PostMapping("/admin/content/approve/{id}")
    public String approveContent(@PathVariable Long id) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson != null) {
            lesson.setStatus("APPROVED");
            lessonRepository.save(lesson);
        }
        return "redirect:/admin/content";
    }

    @PostMapping("/admin/content/reject/{id}")
    public String rejectContent(@PathVariable Long id) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson != null) {
            lesson.setStatus("REJECTED");
            lessonRepository.save(lesson);
        }
        return "redirect:/admin/content";
    }

    @GetMapping("/admin/settings")
    public String settings() {
        return "admin-settings";
    }
}
