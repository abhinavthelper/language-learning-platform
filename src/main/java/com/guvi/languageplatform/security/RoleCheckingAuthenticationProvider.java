package com.guvi.languageplatform.security;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Authenticates users with BCrypt password verification and enforces the selected login role tab.
 */
@Component
public class RoleCheckingAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleCheckingAuthenticationProvider(CustomUserDetailsService userDetailsService,
                                              UserRepository userRepository,
                                              PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String requestedRole = extractRequestedRole();
        if (requestedRole != null && !requestedRole.isEmpty() && !user.getRole().equalsIgnoreCase(requestedRole)) {
            throw new BadCredentialsException(
                    "This is not a " + requestedRole.toLowerCase() + " account. Pick the correct tab.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String extractRequestedRole() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            return request.getParameter("role");
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
