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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Instructor can edit or delete only their own lessons. An edited lesson goes back for approval. */
@Controller
public class InstructorLessonEditController {

    private final LessonRepository lessonRepository;
    private final CurrentUserService currentUserService;

    public InstructorLessonEditController(LessonRepository lessonRepository,
                                          CurrentUserService currentUserService) {
        this.lessonRepository = lessonRepository;
        this.currentUserService = currentUserService;
    }

    /** Returns the lesson only if it exists and belongs to the logged-in instructor. */
    private Lesson ownLesson(Long id, HttpSession session, Authentication authentication) {
        User user = currentUserService.getCurrentUser(session, authentication);
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (user == null || lesson == null || !user.getEmail().equals(lesson.getInstructorEmail())) {
            return null;
        }
        return lesson;
    }

    @GetMapping("/instructor/lessons/{id}/edit")
    public String form(@PathVariable Long id, HttpSession session,
                       Authentication authentication, Model model) {
        Lesson lesson = ownLesson(id, session, authentication);
        if (lesson == null) {
            return "redirect:/instructor/lessons";
        }
        model.addAttribute("lesson", lesson);
        return "instructor-lesson-edit";
    }

    @PostMapping("/instructor/lessons/{id}/edit")
    public String update(@PathVariable Long id, @RequestParam String title, @RequestParam String language,
                         @RequestParam String difficulty, @RequestParam String content,
                         @RequestParam String quizQuestion, @RequestParam String quizAnswer,
                         HttpSession session, Authentication authentication, RedirectAttributes redirect) {
        Lesson lesson = ownLesson(id, session, authentication);
        if (lesson == null) {
            return "redirect:/instructor/lessons";
        }

        String error = InputValidator.validateLessonFields(
                title, language, difficulty, content, quizQuestion, quizAnswer);
        if (error != null) {
            redirect.addFlashAttribute("error", error);
            return "redirect:/instructor/lessons";
        }

        lesson.setTitle(title.trim());
        lesson.setLanguage(language.trim());
        lesson.setDifficulty(difficulty.trim());
        lesson.setContent(content.trim());
        lesson.setQuizQuestion(quizQuestion.trim());
        lesson.setQuizAnswer(quizAnswer.trim());
        lesson.setStatus("PENDING"); // changed content must be approved again
        lessonRepository.save(lesson);
        redirect.addFlashAttribute("success", "Lesson updated. It is waiting for admin approval again.");
        return "redirect:/instructor/lessons";
    }

    @PostMapping("/instructor/lessons/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,
                         Authentication authentication, RedirectAttributes redirect) {
        Lesson lesson = ownLesson(id, session, authentication);
        if (lesson != null) {
            lessonRepository.deleteById(id);
            redirect.addFlashAttribute("success", "Lesson deleted successfully.");
        }
        return "redirect:/instructor/lessons";
    }
}
