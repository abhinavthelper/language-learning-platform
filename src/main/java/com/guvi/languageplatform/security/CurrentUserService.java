package com.guvi.languageplatform.security;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated User from the HTTP session or Spring Security context.
 */
@Component
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(HttpSession session, Authentication authentication) {
        Object sessionUser = session.getAttribute("loggedUser");
        if (sessionUser instanceof User user) {
            return user;
        }
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user != null) {
                session.setAttribute("loggedUser", user);
                return user;
            }
        }
        return null;
    }
}
