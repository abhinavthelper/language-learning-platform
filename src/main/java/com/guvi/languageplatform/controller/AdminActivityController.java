package com.guvi.languageplatform.controller;

import com.guvi.languageplatform.repository.ActivityLogRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Shows the 20 most recent actions (logins, quiz attempts) on the platform. */
@Controller
public class AdminActivityController {

    private final ActivityLogRepository activityLogRepository;

    public AdminActivityController(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping("/admin/activity")
    public String activity(Model model) {
        model.addAttribute("logs", activityLogRepository.findTop20ByOrderByCreatedAtDesc());
        return "admin-activity";
    }
}
