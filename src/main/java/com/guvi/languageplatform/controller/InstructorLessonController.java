package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.repository.LessonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InstructorLessonController {

    private static final String INSTRUCTOR_EMAIL = "inst@mail.com";

    private final LessonRepository lessonRepository;

    public InstructorLessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/instructor/lessons")
    public String list(Model model) {
        model.addAttribute("lessons", lessonRepository.findByInstructorEmail(INSTRUCTOR_EMAIL));
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
                       @RequestParam String quizAnswer) {
        Lesson lesson = new Lesson(title, language, difficulty, content,
                quizQuestion, quizAnswer, "PENDING", INSTRUCTOR_EMAIL);
        lessonRepository.save(lesson);
        return "redirect:/instructor/lessons";
    }
}