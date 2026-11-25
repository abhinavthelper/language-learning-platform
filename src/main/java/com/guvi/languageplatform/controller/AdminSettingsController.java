package com.guvi.languageplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/** Editable system settings. Values are kept in memory (they reset when the server restarts). */
@Controller
public class AdminSettingsController {

    private static final Set<String> ALLOWED_STATUSES = Set.of("Active", "Maintenance");

    // Shared by all requests, so access is synchronized
    private String platformName = "Language Learning Platform";
    private String status = "Active";
    private String welcomeMessage = "Welcome to LingoLearn";

    @GetMapping("/admin/system-settings")
    public synchronized String page(Model model) {
        model.addAttribute("platformName", platformName);
        model.addAttribute("status", status);
        model.addAttribute("welcomeMessage", welcomeMessage);
        return "admin-system-settings";
    }

    @PostMapping("/admin/system-settings")
    public synchronized String save(@RequestParam String platformName, @RequestParam String status,
                                    @RequestParam String welcomeMessage, RedirectAttributes redirect) {
        String cleanName = platformName == null ? "" : platformName.trim();
        String cleanStatus = status == null ? "" : status.trim();
        String cleanWelcome = welcomeMessage == null ? "" : welcomeMessage.trim();

        if (cleanName.isEmpty() || cleanName.length() > 100) {
            redirect.addFlashAttribute("error", "Platform name must be between 1 and 100 characters.");
        } else if (!ALLOWED_STATUSES.contains(cleanStatus)) {
            redirect.addFlashAttribute("error", "Invalid platform status.");
        } else if (cleanWelcome.isEmpty() || cleanWelcome.length() > 250) {
            redirect.addFlashAttribute("error", "Welcome message must be between 1 and 250 characters.");
        } else {
            this.platformName = cleanName;
            this.status = cleanStatus;
            this.welcomeMessage = cleanWelcome;
            redirect.addFlashAttribute("success", "Settings updated successfully.");
        }
        return "redirect:/admin/system-settings";
    }
}
