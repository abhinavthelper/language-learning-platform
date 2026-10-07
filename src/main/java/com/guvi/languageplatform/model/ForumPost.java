package com.guvi.languageplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A message posted by a learner in the discussion forum. */
@Entity
@Table(name = "forum_posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorName;
    private String authorEmail;

    @Column(length = 1000)
    private String message;

    private LocalDateTime postedAt;

    public ForumPost() { }

    public ForumPost(String authorName, String authorEmail, String message) {
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.message = message;
        this.postedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAuthorName() { return authorName; }
    public String getAuthorEmail() { return authorEmail; }
    public String getMessage() { return message; }
    public LocalDateTime getPostedAt() { return postedAt; }
}