package com.guvi.languageplatform.jdbc;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Database operations class using plain JDBC (Connection, PreparedStatement, ResultSet), no JPA. */
@Repository
public class JdbcStatsDao {

    // Only these tables are allowed (prevents SQL injection through the table name)
    private static final List<String> TABLES =
            List.of("users", "lessons", "quiz_attempts", "feedback", "forum_posts", "activity_logs");

    private final DataSource dataSource;

    public JdbcStatsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Counts rows of one table with a raw SQL query. */
    public int countRows(String table) throws SQLException {
        if (!TABLES.contains(table)) {
            throw new IllegalArgumentException("Unknown table: " + table);
        }
        String sql = "SELECT COUNT(*) FROM " + table;
        // try-with-resources closes connection, statement and result set automatically
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Row count of every table, in order. */
    public Map<String, Integer> countAll() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String t : TABLES) {
            result.put(t, countRows(t));
        }
        return result;
    }
}