package com.guvi.languageplatform.repository;

import com.guvi.languageplatform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByStatus(String status);

    List<Lesson> findByInstructorEmail(String instructorEmail);
}