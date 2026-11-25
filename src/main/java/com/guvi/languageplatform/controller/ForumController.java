package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.ForumPost;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.ForumPostRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Discussion forum where learners post messages for each other. */
@Controller
public class ForumController {

    private final ForumPostRepository forumPostRepository;
    private final CurrentUserService currentUserService;

    public ForumController(ForumPostRepository forumPostRepository,
                           CurrentUserService currentUserService) {
        this.forumPostRepository = forumPostRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/learner/forum")
    public String forum(Model model) {
        model.addAttribute("posts", forumPostRepository.findAllByOrderByPostedAtDesc());
        return "learner-forum";
    }

    @PostMapping("/learner/forum")
    public String post(@RequestParam String message, HttpSession session,
                       Authentication authentication, RedirectAttributes redirect) {
        User user = currentUserService.getCurrentUser(session, authentication);
        if (user == null) {
            return "redirect:/login";
        }
        if (InputValidator.isBlankOrTooLong(message, 1000)) {
            redirect.addFlashAttribute("error", "Message must be between 1 and 1000 characters.");
        } else {
            forumPostRepository.save(new ForumPost(user.getName(), user.getEmail(), message.trim()));
            redirect.addFlashAttribute("success", "Your message was posted.");
        }
        return "redirect:/learner/forum";
    }
}
