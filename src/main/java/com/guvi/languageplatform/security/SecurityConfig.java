package com.guvi.languageplatform.security;

import com.guvi.languageplatform.model.ActivityLog;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.ActivityLogRepository;
import com.guvi.languageplatform.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures Spring Security: BCrypt password encoding, role-based route protection,
 * form login with role-tab validation, CSRF protection, and session-invalidating logout.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RoleCheckingAuthenticationProvider authProvider,
                                                   UserRepository userRepository,
                                                   ActivityLogRepository activityLogRepository) throws Exception {
        http
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/error", "/status", "/lesson-stats").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                .requestMatchers("/learner/**").hasRole("LEARNER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    User user = userRepository.findByEmail(authentication.getName());
                    if (user != null) {
                        request.getSession().setAttribute("loggedUser", user);
                        activityLogRepository.save(new ActivityLog(user.getEmail(), "Logged in as " + user.getRole()));
                        String target = switch (user.getRole()) {
                            case "ADMIN" -> "/admin/dashboard";
                            case "INSTRUCTOR" -> "/instructor/dashboard";
                            default -> "/learner/dashboard";
                        };
                        response.sendRedirect(request.getContextPath() + target);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login?error");
                    }
                })
                .failureHandler((request, response, exception) -> {
                    request.getSession().setAttribute("loginError", exception.getMessage());
                    response.sendRedirect(request.getContextPath() + "/login?error");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
