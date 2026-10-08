package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.Feedback;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.FeedbackRepository;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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

    public InstructorFeedbackController(FeedbackRepository feedbackRepository,
                                        UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/instructor/feedback")
    public String page(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }
        // Only learners can receive feedback
        List<User> learners = userRepository.findAll().stream()
                .filter(u -> "LEARNER".equals(u.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("learners", learners);
        model.addAttribute("feedbackList", feedbackRepository.findByInstructorEmail(user.getEmail()));
        return "instructor-feedback";
    }

    @PostMapping("/instructor/feedback")
    public String save(@RequestParam String learnerEmail, @RequestParam String message,
                       HttpSession session, RedirectAttributes redirect) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (message.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "Feedback cannot be empty.");
        } else {
            feedbackRepository.save(new Feedback(user.getEmail(), learnerEmail, message.trim()));
            redirect.addFlashAttribute("success", "Feedback submitted successfully.");
        }
        return "redirect:/instructor/feedback";
    }
}