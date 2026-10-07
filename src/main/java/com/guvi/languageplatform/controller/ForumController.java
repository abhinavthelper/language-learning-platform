package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.model.ForumPost;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.ForumPostRepository;
import jakarta.servlet.http.HttpSession;
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

    public ForumController(ForumPostRepository forumPostRepository) {
        this.forumPostRepository = forumPostRepository;
    }

    @GetMapping("/learner/forum")
    public String forum(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("posts", forumPostRepository.findAllByOrderByPostedAtDesc());
        return "learner-forum";
    }

    @PostMapping("/learner/forum")
    public String post(@RequestParam String message, HttpSession session, RedirectAttributes redirect) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (message.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "Message cannot be empty.");
        } else {
            forumPostRepository.save(new ForumPost(user.getName(), user.getEmail(), message.trim()));
            redirect.addFlashAttribute("success", "Your message was posted.");
        }
        return "redirect:/learner/forum";
    }
}