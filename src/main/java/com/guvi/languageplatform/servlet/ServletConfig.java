package com.guvi.languageplatform.servlet;

import jakarta.servlet.Servlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers our HttpServlets with the embedded server.
 * /status       -> StatusServlet       (table counts, reports, visit counter)
 * /lesson-stats -> LessonStatsServlet  (lesson statistics using plain JDBC)
 */
@Configuration
public class ServletConfig {

    @Bean
    public StatusServlet statusServlet() {
        return new StatusServlet();
    }

    @Bean
    public ServletRegistrationBean<Servlet> statusServletRegistration(StatusServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/status");
    }

    @Bean
    public LessonStatsServlet lessonStatsServlet() {
        return new LessonStatsServlet();
    }

    @Bean
    public ServletRegistrationBean<Servlet> lessonStatsServletRegistration(LessonStatsServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/lesson-stats");
    }
}