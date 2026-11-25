package com.guvi.languageplatform;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           LessonRepository lessonRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Admin One", "admin@mail.com", passwordEncoder.encode("admin123"), "ADMIN"));
            userRepository.save(new User("Instructor One", "inst@mail.com", passwordEncoder.encode("inst123"), "INSTRUCTOR"));
            userRepository.save(new User("Learner One", "learner@mail.com", passwordEncoder.encode("learn123"), "LEARNER"));
        } else {
            // Migrate any legacy plaintext passwords to BCrypt on startup
            for (User u : userRepository.findAll()) {
                String pw = u.getPassword();
                if (pw != null && !pw.startsWith("$2a$") && !pw.startsWith("$2b$") && !pw.startsWith("$2y$")) {
                    u.setPassword(passwordEncoder.encode(pw));
                    userRepository.save(u);
                }
            }
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