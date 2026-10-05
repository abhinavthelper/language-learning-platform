package com.guvi.languageplatform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String language;
    private String difficulty;

    @Column(length = 2000)
    private String content;

    private String quizQuestion;
    private String quizAnswer;

    private String status;          
    private String instructorEmail;  

    public Lesson() { }

    public Lesson(String title, String language, String difficulty, String content,
                  String quizQuestion, String quizAnswer, String status, String instructorEmail) {
        this.title = title;
        this.language = language;
        this.difficulty = difficulty;
        this.content = content;
        this.quizQuestion = quizQuestion;
        this.quizAnswer = quizAnswer;
        this.status = status;
        this.instructorEmail = instructorEmail;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getQuizQuestion() { return quizQuestion; }
    public void setQuizQuestion(String quizQuestion) { this.quizQuestion = quizQuestion; }

    public String getQuizAnswer() { return quizAnswer; }
    public void setQuizAnswer(String quizAnswer) { this.quizAnswer = quizAnswer; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInstructorEmail() { return instructorEmail; }
    public void setInstructorEmail(String instructorEmail) { this.instructorEmail = instructorEmail; }
}