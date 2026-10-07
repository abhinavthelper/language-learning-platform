package com.guvi.languageplatform.core;

import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.repository.LessonRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Lessons grouped by approval status. */
@Component
public class LessonReport extends AbstractReport {

    private final LessonRepository lessonRepository;

    public LessonReport(LessonRepository lessonRepository) {
        super("Lessons by Status");
        this.lessonRepository = lessonRepository;
    }

    @Override
    protected String buildBody() {
        List<Lesson> lessons = lessonRepository.findAll();
        Map<String, Long> byStatus = lessons.stream()
                .collect(Collectors.groupingBy(Lesson::getStatus, TreeMap::new, Collectors.counting()));
        StringBuilder sb = new StringBuilder();
        byStatus.forEach((status, count) -> sb.append(status).append(": ").append(count).append("\n"));
        return sb.toString();
    }
}