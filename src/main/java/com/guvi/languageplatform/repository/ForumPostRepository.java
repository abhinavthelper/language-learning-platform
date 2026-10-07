package com.guvi.languageplatform.repository;

import com.guvi.languageplatform.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByOrderByPostedAtDesc();
}