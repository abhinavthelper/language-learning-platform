package com.guvi.languageplatform;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Admin One", "admin@mail.com", "admin123", "ADMIN"));
            userRepository.save(new User("Instructor One", "inst@mail.com", "inst123", "INSTRUCTOR"));
            userRepository.save(new User("Learner One", "learner@mail.com", "learn123", "LEARNER"));
        }
    }
}