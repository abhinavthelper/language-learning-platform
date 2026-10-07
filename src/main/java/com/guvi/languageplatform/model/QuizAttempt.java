package com.guvi.languageplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Stores one quiz attempt made by a learner. */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String learnerEmail;
    private Long lessonId;
    private String lessonTitle;
    private String instructorEmail;
    private boolean correct;
    private LocalDateTime attemptedAt;

    public QuizAttempt() { }

    public QuizAttempt(String learnerEmail, Long lessonId, String lessonTitle,
                       String instructorEmail, boolean correct) {
        this.learnerEmail = learnerEmail;
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.instructorEmail = instructorEmail;
        this.correct = correct;
        this.attemptedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getLearnerEmail() { return learnerEmail; }
    public Long getLessonId() { return lessonId; }
    public String getLessonTitle() { return lessonTitle; }
    public String getInstructorEmail() { return instructorEmail; }
    public boolean isCorrect() { return correct; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
}