package com.guvi.languageplatform.servlet;

import com.guvi.languageplatform.jdbc.JdbcLessonDao;
import com.guvi.languageplatform.jdbc.JdbcLessonDao.LessonRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * HttpServlet mapped to /lesson-stats.
 * Shows lesson counts (JDBC GROUP BY) and a table of lessons filtered by the "status" request parameter.
 * Example: /lesson-stats?status=PENDING
 */
public class LessonStatsServlet extends HttpServlet {

    // Only these values are accepted from the request parameter
    private static final List<String> STATUSES = List.of("PENDING", "APPROVED", "REJECTED");

    private JdbcLessonDao lessonDao;

    @Override
    public void init() throws ServletException {
        // The container creates servlets itself, so the bean is fetched from Spring here
        WebApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        lessonDao = ctx.getBean(JdbcLessonDao.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Read the request parameter and fall back to APPROVED if it is missing or invalid
        String status = req.getParameter("status");
        status = (status != null && STATUSES.contains(status.toUpperCase())) ? status.toUpperCase() : "APPROVED";

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("<title>Lesson Statistics</title>");
        out.println("<style>body{font-family:Arial;margin:24px;} table{border-collapse:collapse;margin:8px 0 24px;}"
                + " th,td{border:1px solid #ccc;padding:8px 14px;text-align:left;} a{margin-right:14px;}</style>");
        out.println("</head><body>");
        out.println("<h1>Lesson Statistics (JDBC)</h1>");

        try {
            out.println("<h3>Lessons by status</h3>");
            printCounts(out, "Status", lessonDao.countByStatus());

            out.println("<h3>Lessons by language</h3>");
            printCounts(out, "Language", lessonDao.countByLanguage());

            out.println("<h3>Lessons with status " + status + "</h3>");
            out.println("<p>Filter: <a href=\"?status=APPROVED\">APPROVED</a>"
                    + "<a href=\"?status=PENDING\">PENDING</a>"
                    + "<a href=\"?status=REJECTED\">REJECTED</a></p>");
            printLessons(out, lessonDao.findByStatus(status));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<p>Database error: " + escape(e.getMessage()) + "</p>");
        }

        out.println("</body></html>");
    }

    /** Prints a two-column table from a map of label to count. */
    private void printCounts(PrintWriter out, String heading, java.util.Map<String, Integer> counts) {
        out.println("<table><tr><th>" + heading + "</th><th>Lessons</th></tr>");
        counts.forEach((label, count) ->
                out.println("<tr><td>" + escape(label) + "</td><td>" + count + "</td></tr>"));
        out.println("</table>");
    }

    /** Prints one row per lesson. */
    private void printLessons(PrintWriter out, List<LessonRow> lessons) {
        out.println("<table><tr><th>ID</th><th>Title</th><th>Language</th><th>Difficulty</th><th>Instructor</th></tr>");
        for (LessonRow l : lessons) {
            out.println("<tr><td>" + l.id() + "</td><td>" + escape(l.title()) + "</td><td>" + escape(l.language())
                    + "</td><td>" + escape(l.difficulty()) + "</td><td>" + escape(l.instructorEmail()) + "</td></tr>");
        }
        out.println("</table>");
    }

    /** Escapes HTML special characters so stored text cannot inject markup. */
    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}