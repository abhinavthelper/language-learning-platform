package com.guvi.languageplatform.core;

/** Interface: every report in the system must follow this contract. */
public interface Reportable {
    String getReportTitle();
    String generate();
}