package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Feedback;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.FeedbackRepository;
import com.guvi.languageplatform.repository.UserRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/** Lets an instructor write feedback for learners and see feedback already given. */
@Controller
public class InstructorFeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public InstructorFeedbackController(FeedbackRepository feedbackRepository,
                                        UserRepository userRepository,
                                        CurrentUserService currentUserService) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/instructor/feedback")
    public String page(HttpSession session, Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }
        List<User> learners = userRepository.findAll().stream()
                .filter(u -> "LEARNER".equals(u.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("learners", learners);
        model.addAttribute("feedbackList", feedbackRepository.findByInstructorEmail(user.getEmail()));
        return "instructor-feedback";
    }

    @PostMapping("/instructor/feedback")
    public String save(@RequestParam String learnerEmail, @RequestParam String message,
                       HttpSession session, Authentication authentication, RedirectAttributes redirect) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }
        User learner = learnerEmail == null ? null : userRepository.findByEmail(learnerEmail.trim());
        if (learner == null || !"LEARNER".equals(learner.getRole())) {
            redirect.addFlashAttribute("error", "Please select a valid learner.");
        } else if (InputValidator.isBlankOrTooLong(message, 1000)) {
            redirect.addFlashAttribute("error", "Feedback must be between 1 and 1000 characters.");
        } else {
            feedbackRepository.save(new Feedback(user.getEmail(), learner.getEmail(), message.trim()));
            redirect.addFlashAttribute("success", "Feedback submitted successfully.");
        }
        return "redirect:/instructor/feedback";
    }
}
