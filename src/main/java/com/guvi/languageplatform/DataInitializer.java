package com.guvi.languageplatform;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public DataInitializer(UserRepository userRepository, LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Admin One", "admin@mail.com", "admin123", "ADMIN"));
            userRepository.save(new User("Instructor One", "inst@mail.com", "inst123", "INSTRUCTOR"));
            userRepository.save(new User("Learner One", "learner@mail.com", "learn123", "LEARNER"));
        }

        if (lessonRepository.count() == 0) {
            lessonRepository.save(new Lesson(
                    "Spanish Basics", "Spanish", "Beginner",
                    "Hola means Hello. Adios means Goodbye. Gracias means Thank you.",
                    "What does Hola mean?", "Hello",
                    "APPROVED", "inst@mail.com"));

            lessonRepository.save(new Lesson(
                    "French Greetings", "French", "Beginner",
                    "Bonjour means Good morning. Merci means Thank you.",
                    "What does Merci mean?", "Thank you",
                    "PENDING", "inst@mail.com"));
        }
    }
}