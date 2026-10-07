package com.guvi.languageplatform.core;

import org.springframework.stereotype.Component;

/**
 * Every request runs on its own thread, so many users can hit this at once.
 * synchronized makes sure the counter is never updated by two threads together.
 */
@Component
public class VisitCounter {

    private int visits = 0;

    public synchronized int hit() {
        visits++;
        return visits;
    }

    public synchronized int getVisits() {
        return visits;
    }
}
