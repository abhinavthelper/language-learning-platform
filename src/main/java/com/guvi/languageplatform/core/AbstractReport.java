package com.guvi.languageplatform.core;

/**
 * Abstract base class (inheritance).
 * generate() is fixed here; child classes only supply buildBody().
 */
public abstract class AbstractReport implements Reportable {

    private final String title;

    protected AbstractReport(String title) {
        this.title = title;
    }

    @Override
    public String getReportTitle() {
        return title;
    }

    @Override
    public String generate() {
        return "== " + title + " ==\n" + buildBody();
    }

    /** Each child report builds its own body. */
    protected abstract String buildBody();
}