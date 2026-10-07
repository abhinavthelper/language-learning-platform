package com.guvi.languageplatform.servlet;

import com.guvi.languageplatform.core.ReportService;
import com.guvi.languageplatform.core.VisitCounter;
import com.guvi.languageplatform.jdbc.JdbcStatsDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * A real HttpServlet mapped to /status.
 * The container creates servlets itself, so beans are fetched from Spring in init().
 */
public class StatusServlet extends HttpServlet {

    private JdbcStatsDao statsDao;
    private ReportService reportService;
    private VisitCounter visitCounter;

    @Override
    public void init() throws ServletException {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        statsDao = ctx.getBean(JdbcStatsDao.class);
        reportService = ctx.getBean(ReportService.class);
        visitCounter = ctx.getBean(VisitCounter.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("Language Learning Platform - Status");
        out.println("Visits: " + visitCounter.hit());
        out.println();
        try {
            out.println("Table row counts (JDBC):");
            statsDao.countAll().forEach((table, count) -> out.println("  " + table + ": " + count));
        } catch (SQLException e) {
            out.println("Database error: " + e.getMessage());
        }
        out.println();
        out.println(reportService.generateAll());
    }
}