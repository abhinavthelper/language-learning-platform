package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InstructorLessonController {

    private final LessonRepository lessonRepository;
    private final CurrentUserService currentUserService;

    public InstructorLessonController(LessonRepository lessonRepository,
                                      CurrentUserService currentUserService) {
        this.lessonRepository = lessonRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/instructor/lessons")
    public String list(HttpSession session, Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("lessons", lessonRepository.findByInstructorEmail(user.getEmail()));
        return "instructor-lessons";
    }

    @GetMapping("/instructor/lessons/new")
    public String form() {
        return "instructor-lesson-form";
    }

    @PostMapping("/instructor/lessons")
    public String save(@RequestParam String title,
                       @RequestParam String language,
                       @RequestParam String difficulty,
                       @RequestParam String content,
                       @RequestParam String quizQuestion,
                       @RequestParam String quizAnswer,
                       HttpSession session,
                       Authentication authentication,
                       RedirectAttributes redirect) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }

        String error = InputValidator.validateLessonFields(
                title, language, difficulty, content, quizQuestion, quizAnswer);
        if (error != null) {
            redirect.addFlashAttribute("error", error);
            return "redirect:/instructor/lessons";
        }

        Lesson lesson = new Lesson(
                title.trim(), language.trim(), difficulty.trim(), content.trim(),
                quizQuestion.trim(), quizAnswer.trim(), "PENDING", user.getEmail());
        lessonRepository.save(lesson);
        redirect.addFlashAttribute("success", "Lesson submitted for admin approval.");
        return "redirect:/instructor/lessons";
    }
}
