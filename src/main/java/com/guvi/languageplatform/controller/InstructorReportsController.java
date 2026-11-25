package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.QuizAttempt;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.QuizAttemptRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Learner progress table and lesson analytics, both built from quiz attempts. */
@Controller
public class InstructorReportsController {

    private final QuizAttemptRepository quizAttemptRepository;
    private final CurrentUserService currentUserService;

    public InstructorReportsController(QuizAttemptRepository quizAttemptRepository,
                                       CurrentUserService currentUserService) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.currentUserService = currentUserService;
    }

    /** One row of a report: a label, attempts and correct answers. */
    public static class Stat {
        private final String label;
        private final long attempts;
        private final long correct;

        public Stat(String label, long attempts, long correct) {
            this.label = label;
            this.attempts = attempts;
            this.correct = correct;
        }

        public String getLabel() { return label; }
        public long getAttempts() { return attempts; }
        public long getCorrect() { return correct; }
        public long getPercent() { return attempts == 0 ? 0 : correct * 100 / attempts; }
    }

    /** Groups attempts by any key (learner or lesson) using a Function. */
    private List<Stat> buildStats(List<QuizAttempt> attempts, Function<QuizAttempt, String> keyFn) {
        Map<String, List<QuizAttempt>> grouped = attempts.stream()
                .collect(Collectors.groupingBy(keyFn, TreeMap::new, Collectors.toList()));
        List<Stat> result = new ArrayList<>();
        grouped.forEach((key, list) -> result.add(new Stat(
                key, list.size(), list.stream().filter(QuizAttempt::isCorrect).count())));
        return result;
    }

    private String report(HttpSession session, Authentication authentication, Model model,
                          String title, String subtitle,
                          String firstColumn, Function<QuizAttempt, String> keyFn) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }
        List<QuizAttempt> attempts = quizAttemptRepository.findByInstructorEmail(user.getEmail());
        long total = attempts.size();
        long correct = attempts.stream().filter(QuizAttempt::isCorrect).count();

        model.addAttribute("title", title);
        model.addAttribute("subtitle", subtitle);
        model.addAttribute("firstColumn", firstColumn);
        model.addAttribute("total", total);
        model.addAttribute("correct", correct);
        model.addAttribute("percent", total == 0 ? 0 : correct * 100 / total);
        model.addAttribute("stats", buildStats(attempts, keyFn));
        return "instructor-report";
    }

    @GetMapping("/instructor/progress")
    public String learnerProgress(HttpSession session, Authentication authentication, Model model) {
        return report(session, authentication, model, "Learner Progress",
                "How each learner performs on your lessons.", "Learner", QuizAttempt::getLearnerEmail);
    }

    @GetMapping("/instructor/analytics")
    public String analytics(HttpSession session, Authentication authentication, Model model) {
        return report(session, authentication, model, "Lesson Analytics",
                "Engagement and performance for each of your lessons.", "Lesson", QuizAttempt::getLessonTitle);
    }
}
