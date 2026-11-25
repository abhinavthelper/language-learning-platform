package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.QuizAttempt;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.FeedbackRepository;
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
import java.util.stream.Collectors;

/** Shows a learner's quiz progress and the feedback received from instructors. */
@Controller
public class LearnerProgressController {

    private final QuizAttemptRepository quizAttemptRepository;
    private final FeedbackRepository feedbackRepository;
    private final CurrentUserService currentUserService;

    public LearnerProgressController(QuizAttemptRepository quizAttemptRepository,
                                     FeedbackRepository feedbackRepository,
                                     CurrentUserService currentUserService) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.feedbackRepository = feedbackRepository;
        this.currentUserService = currentUserService;
    }

    /** Result numbers for one lesson (used by the bar chart on the page). */
    public static class LessonStat {
        private final String title;
        private final long attempts;
        private final long correct;

        public LessonStat(String title, long attempts, long correct) {
            this.title = title;
            this.attempts = attempts;
            this.correct = correct;
        }

        public String getTitle() { return title; }
        public long getAttempts() { return attempts; }
        public long getCorrect() { return correct; }
        public long getPercent() { return attempts == 0 ? 0 : correct * 100 / attempts; }
    }

    @GetMapping("/learner/progress")
    public String progress(HttpSession session, Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }

        List<QuizAttempt> attempts = quizAttemptRepository.findByLearnerEmail(user.getEmail());
        long total = attempts.size();
        long correct = attempts.stream().filter(QuizAttempt::isCorrect).count();

        // Group attempts by lesson title using streams
        Map<String, List<QuizAttempt>> byLesson = attempts.stream()
                .collect(Collectors.groupingBy(QuizAttempt::getLessonTitle, TreeMap::new, Collectors.toList()));

        List<LessonStat> stats = new ArrayList<>();
        byLesson.forEach((title, list) -> stats.add(new LessonStat(
                title, list.size(), list.stream().filter(QuizAttempt::isCorrect).count())));

        model.addAttribute("total", total);
        model.addAttribute("correct", correct);
        model.addAttribute("percent", total == 0 ? 0 : correct * 100 / total);
        model.addAttribute("stats", stats);
        model.addAttribute("feedbackList", feedbackRepository.findByLearnerEmail(user.getEmail()));
        return "learner-progress";
    }
}
