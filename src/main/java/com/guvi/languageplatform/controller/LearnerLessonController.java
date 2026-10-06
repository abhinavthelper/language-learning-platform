package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.repository.LessonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LearnerLessonController {

    private final LessonRepository lessonRepository;

    public LearnerLessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/learner/lessons")
    public String list(Model model) {
        model.addAttribute("lessons", lessonRepository.findByStatus("APPROVED"));
        return "learner-lessons";
    }

    @GetMapping("/learner/lessons/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null || !"APPROVED".equals(lesson.getStatus())) {
            return "redirect:/learner/lessons";
        }
        model.addAttribute("lesson", lesson);
        return "learner-lesson-detail";
    }

    @PostMapping("/learner/lessons/{id}/quiz")
    public String checkQuiz(@PathVariable Long id, @RequestParam String answer, Model model) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null || !"APPROVED".equals(lesson.getStatus())) {
            return "redirect:/learner/lessons";
        }
        boolean correct = lesson.getQuizAnswer().trim().equalsIgnoreCase(answer.trim());
        model.addAttribute("lesson", lesson);
        model.addAttribute("result", correct ? "Correct! 🎉" : "Wrong, try again.");
        return "learner-lesson-detail";
    }
}