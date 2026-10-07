package com.guvi.languageplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Feedback written by an instructor for a learner. */
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String instructorEmail;
    private String learnerEmail;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;

    public Feedback() { }

    public Feedback(String instructorEmail, String learnerEmail, String message) {
        this.instructorEmail = instructorEmail;
        this.learnerEmail = learnerEmail;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getInstructorEmail() { return instructorEmail; }
    public String getLearnerEmail() { return learnerEmail; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}