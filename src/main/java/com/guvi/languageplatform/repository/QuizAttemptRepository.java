package com.guvi.languageplatform.repository;

import com.guvi.languageplatform.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByLearnerEmail(String learnerEmail);
    List<QuizAttempt> findByInstructorEmail(String instructorEmail);
}