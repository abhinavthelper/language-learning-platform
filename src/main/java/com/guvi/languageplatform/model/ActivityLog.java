package com.guvi.languageplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Records who did what and when (used for admin activity monitoring). */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String action;
    private LocalDateTime createdAt;

    public ActivityLog() { }

    public ActivityLog(String userEmail, String action) {
        this.userEmail = userEmail;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getAction() { return action; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}