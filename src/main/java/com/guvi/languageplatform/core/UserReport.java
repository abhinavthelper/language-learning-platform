package com.guvi.languageplatform.core;

import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Users grouped by role. Uses List, Map and Streams (Collections and Generics). */
@Component
public class UserReport extends AbstractReport {

    private final UserRepository userRepository;

    public UserReport(UserRepository userRepository) {
        super("Users by Role");
        this.userRepository = userRepository;
    }

    @Override
    protected String buildBody() {
        List<User> users = userRepository.findAll();
        Map<String, Long> byRole = users.stream()
                .collect(Collectors.groupingBy(User::getRole, TreeMap::new, Collectors.counting()));
        StringBuilder sb = new StringBuilder();
        byRole.forEach((role, count) -> sb.append(role).append(": ").append(count).append("\n"));
        return sb.toString();
    }
}