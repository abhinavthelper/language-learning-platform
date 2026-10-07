package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.ActivityLog;
import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.QuizAttempt;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.ActivityLogRepository;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.QuizAttemptRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LearnerLessonController {

    private final LessonRepository lessonRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ActivityLogRepository activityLogRepository;

    public LearnerLessonController(LessonRepository lessonRepository,
                                   QuizAttemptRepository quizAttemptRepository,
                                   ActivityLogRepository activityLogRepository) {
        this.lessonRepository = lessonRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.activityLogRepository = activityLogRepository;
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
    public String checkQuiz(@PathVariable Long id, @RequestParam String answer,
                            HttpSession session, Model model) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null || !"APPROVED".equals(lesson.getStatus())) {
            return "redirect:/learner/lessons";
        }
        boolean correct = lesson.getQuizAnswer().trim().equalsIgnoreCase(answer.trim());

        // Save the attempt so progress, analytics and activity pages can use it
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            quizAttemptRepository.save(new QuizAttempt(
                    user.getEmail(), lesson.getId(), lesson.getTitle(),
                    lesson.getInstructorEmail(), correct));
            activityLogRepository.save(new ActivityLog(user.getEmail(),
                    "Quiz on '" + lesson.getTitle() + "': " + (correct ? "correct" : "wrong")));
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("result", correct ? "Correct! 🎉" : "Wrong, try again.");
        return "learner-lesson-detail";
    }
}