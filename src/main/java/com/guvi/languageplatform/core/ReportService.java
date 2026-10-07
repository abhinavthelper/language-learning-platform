package com.guvi.languageplatform.core;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Polymorphism: this class only knows the Reportable interface.
 * Spring injects every implementation (UserReport, LessonReport, ...).
 */
@Service
public class ReportService {

    private final List<Reportable> reports;

    public ReportService(List<Reportable> reports) {
        this.reports = reports;
    }

    /** Runs every report through the same interface call. */
    public String generateAll() {
        StringBuilder sb = new StringBuilder();
        for (Reportable r : reports) {
            sb.append(r.generate()).append("\n");
        }
        return sb.toString();
    }

    /** Throws our custom exception if the report title is unknown. */
    public String generateByTitle(String title) {
        for (Reportable r : reports) {
            if (r.getReportTitle().equalsIgnoreCase(title)) {
                return r.generate();
            }
        }
        throw new ResourceNotFoundException("No report named: " + title);
    }
}