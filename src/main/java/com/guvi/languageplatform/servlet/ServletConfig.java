package com.guvi.languageplatform.servlet;

import jakarta.servlet.Servlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers our HttpServlet with the embedded server.
 * The servlet receives its dependencies through the constructor.
 */
@Configuration
public class ServletConfig {

    @Bean
    public StatusServlet statusServlet() {
        return new StatusServlet();
    }

    @Bean
    public org.springframework.boot.web.servlet.ServletRegistrationBean<Servlet> statusServletRegistration(StatusServlet servlet) {
        return new org.springframework.boot.web.servlet.ServletRegistrationBean<>(servlet, "/status");
    }
}