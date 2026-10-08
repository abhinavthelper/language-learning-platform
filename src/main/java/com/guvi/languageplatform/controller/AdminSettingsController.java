package com.guvi.languageplatform.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Editable system settings. Values are kept in memory (they reset when the server restarts). */
@Controller
public class AdminSettingsController {

    // Shared by all requests, so access is synchronized
    private String platformName = "Language Learning Platform";
    private String status = "Active";
    private String welcomeMessage = "Welcome to LingoLearn";

    @GetMapping("/admin/system-settings")
    public synchronized String page(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("platformName", platformName);
        model.addAttribute("status", status);
        model.addAttribute("welcomeMessage", welcomeMessage);
        return "admin-system-settings";
    }

    @PostMapping("/admin/system-settings")
    public synchronized String save(@RequestParam String platformName, @RequestParam String status,
                                    @RequestParam String welcomeMessage, RedirectAttributes redirect) {
        if (platformName.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "Platform name cannot be empty.");
        } else {
            this.platformName = platformName.trim();
            this.status = status;
            this.welcomeMessage = welcomeMessage.trim();
            redirect.addFlashAttribute("success", "Settings updated successfully.");
        }
        return "redirect:/admin/system-settings";
    }
}
