package com.guvi.languageplatform;

import com.guvi.languageplatform.jdbc.JdbcLessonDao;
import com.guvi.languageplatform.jdbc.JdbcStatsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcIntegrationTest {

    @Autowired
    private JdbcLessonDao jdbcLessonDao;

    @Autowired
    private JdbcStatsDao jdbcStatsDao;

    @Test
    void jdbcLessonDaoCrudAndAggregationsWorkDirectlyAgainstH2() throws Exception {
        String title = "JDBC Test Lesson " + System.currentTimeMillis();
        int inserted = jdbcLessonDao.insert(
                title, "Italian", "Intermediate",
                "Ciao means Hello.", "What does Ciao mean?", "Hello",
                "PENDING", "inst@mail.com");
        assertEquals(1, inserted);

        List<JdbcLessonDao.LessonRow> pending = jdbcLessonDao.findByStatus("PENDING");
        JdbcLessonDao.LessonRow created = pending.stream()
                .filter(r -> title.equals(r.title()))
                .findFirst()
                .orElseThrow();

        assertEquals("Italian", created.language());
        assertEquals("Intermediate", created.difficulty());
        assertEquals("PENDING", created.status());

        int updated = jdbcLessonDao.updateStatus(created.id(), "APPROVED");
        assertEquals(1, updated);

        List<JdbcLessonDao.LessonRow> approved = jdbcLessonDao.findByStatus("APPROVED");
        assertTrue(approved.stream().anyMatch(r -> r.id() == created.id() && "APPROVED".equals(r.status())));

        Map<String, Integer> byStatus = jdbcLessonDao.countByStatus();
        assertTrue(byStatus.getOrDefault("APPROVED", 0) >= 1);

        Map<String, Integer> byLang = jdbcLessonDao.countByLanguage();
        assertTrue(byLang.getOrDefault("Italian", 0) >= 1);

        int deleted = jdbcLessonDao.delete(created.id());
        assertEquals(1, deleted);
        assertFalse(jdbcLessonDao.findByStatus("APPROVED").stream().anyMatch(r -> r.id() == created.id()));
    }

    @Test
    void jdbcStatsDaoCountsAllTablesAndRejectsUnknownTables() throws Exception {
        Map<String, Integer> counts = jdbcStatsDao.countAll();
        assertEquals(6, counts.size());
        assertTrue(counts.containsKey("users"));
        assertTrue(counts.containsKey("lessons"));
        assertTrue(counts.containsKey("quiz_attempts"));
        assertTrue(counts.containsKey("feedback"));
        assertTrue(counts.containsKey("forum_posts"));
        assertTrue(counts.containsKey("activity_logs"));

        assertTrue(jdbcStatsDao.countRows("users") >= 3);
        InvalidDataAccessApiUsageException ex = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> jdbcStatsDao.countRows("users; DROP TABLE users;"));
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }
}
