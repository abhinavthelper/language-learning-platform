package com.guvi.languageplatform;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SecurityAndCsrfIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void seededUsersHaveBCryptPasswords() {
        User admin = userRepository.findByEmail("admin@mail.com");
        User inst = userRepository.findByEmail("inst@mail.com");
        User learner = userRepository.findByEmail("learner@mail.com");

        assertNotNull(admin);
        assertNotNull(inst);
        assertNotNull(learner);

        assertTrue(admin.getPassword().startsWith("$2"));
        assertTrue(inst.getPassword().startsWith("$2"));
        assertTrue(learner.getPassword().startsWith("$2"));

        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()));
        assertTrue(passwordEncoder.matches("inst123", inst.getPassword()));
        assertTrue(passwordEncoder.matches("learn123", learner.getPassword()));
    }

    @Test
    void loginEnforcesMatchingRoleTab() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "admin@mail.com")
                        .param("password", "admin123")
                        .param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "admin@mail.com")
                        .param("password", "admin123")
                        .param("role", "LEARNER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void unauthenticatedRequestsRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/admin/users")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/admin/system-settings")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/admin/activity")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/instructor/lessons")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/learner/dashboard")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
    }

    @Test
    void roleBasedAuthorizationBlocksCrossRoleAccess() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("learner@mail.com").roles("LEARNER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/system-settings").with(user("learner@mail.com").roles("LEARNER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/activity").with(user("learner@mail.com").roles("LEARNER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/instructor/lessons").with(user("learner@mail.com").roles("LEARNER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/content").with(user("inst@mail.com").roles("INSTRUCTOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/learner/dashboard").with(user("inst@mail.com").roles("INSTRUCTOR")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/instructor/lessons").with(user("admin@mail.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/learner/lessons").with(user("admin@mail.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void postWithoutCsrfTokenIsRejected() throws Exception {
        mockMvc.perform(post("/admin/users/create")
                        .with(user("admin@mail.com").roles("ADMIN"))
                        .param("name", "No Csrf")
                        .param("email", "nocsrf@mail.com")
                        .param("password", "secret123")
                        .param("role", "LEARNER"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/instructor/lessons")
                        .with(user("inst@mail.com").roles("INSTRUCTOR"))
                        .param("title", "No Csrf Lesson")
                        .param("language", "Spanish")
                        .param("difficulty", "Beginner")
                        .param("content", "Content")
                        .param("quizQuestion", "Q?")
                        .param("quizAnswer", "A"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/learner/forum")
                        .with(user("learner@mail.com").roles("LEARNER"))
                        .param("message", "Hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreateUserEncodesPasswordAndValidatesInput() throws Exception {
        String email = "newlearner+" + System.currentTimeMillis() + "@mail.com";

        // Invalid short password is rejected with flash error
        mockMvc.perform(post("/admin/users/create")
                        .with(user("admin@mail.com").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Short Pw")
                        .param("email", email)
                        .param("password", "123")
                        .param("role", "LEARNER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
        assertNull(userRepository.findByEmail(email));

        // Valid creation hashes password with BCrypt
        mockMvc.perform(post("/admin/users/create")
                        .with(user("admin@mail.com").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Valid Learner")
                        .param("email", email)
                        .param("password", "strongPass1")
                        .param("role", "LEARNER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        User created = userRepository.findByEmail(email);
        assertNotNull(created);
        assertTrue(created.getPassword().startsWith("$2"));
        assertTrue(passwordEncoder.matches("strongPass1", created.getPassword()));

        userRepository.delete(created);
    }

    @Test
    void instructorCreateLessonUsesAuthenticatedEmailAndValidates() throws Exception {
        // Blank title rejected
        mockMvc.perform(post("/instructor/lessons")
                        .with(user("inst@mail.com").roles("INSTRUCTOR"))
                        .with(csrf())
                        .param("title", "   ")
                        .param("language", "German")
                        .param("difficulty", "Beginner")
                        .param("content", "Hallo means Hello.")
                        .param("quizQuestion", "What is Hallo?")
                        .param("quizAnswer", "Hello"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));

        String uniqueTitle = "German Basics " + System.currentTimeMillis();
        mockMvc.perform(post("/instructor/lessons")
                        .with(user("inst@mail.com").roles("INSTRUCTOR"))
                        .with(csrf())
                        .param("title", uniqueTitle)
                        .param("language", "German")
                        .param("difficulty", "Beginner")
                        .param("content", "Hallo means Hello.")
                        .param("quizQuestion", "What is Hallo?")
                        .param("quizAnswer", "Hello"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        var saved = lessonRepository.findByInstructorEmail("inst@mail.com").stream()
                .filter(l -> uniqueTitle.equals(l.getTitle()))
                .findFirst();
        assertTrue(saved.isPresent());
        assertEquals("PENDING", saved.get().getStatus());

        lessonRepository.delete(saved.get());
    }
}
