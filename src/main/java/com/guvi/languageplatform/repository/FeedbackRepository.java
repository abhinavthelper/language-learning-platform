package com.guvi.languageplatform.repository;

import com.guvi.languageplatform.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByLearnerEmail(String learnerEmail);
    List<Feedback> findByInstructorEmail(String instructorEmail);
}