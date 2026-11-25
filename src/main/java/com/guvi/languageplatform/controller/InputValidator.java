package com.guvi.languageplatform.controller;

import java.util.Set;

/**
 * Shared server-side input validation helpers for controller form submissions.
 */
final class InputValidator {

    private static final Set<String> ALLOWED_DIFFICULTIES = Set.of("Beginner", "Intermediate", "Advanced");

    private InputValidator() {
    }

    static String validateLessonFields(String title, String language, String difficulty,
                                       String content, String quizQuestion, String quizAnswer) {
        if (isBlankOrTooLong(title, 150)) {
            return "Lesson title must be between 1 and 150 characters.";
        }
        if (isBlankOrTooLong(language, 80)) {
            return "Language must be between 1 and 80 characters.";
        }
        if (difficulty == null || !ALLOWED_DIFFICULTIES.contains(difficulty.trim())) {
            return "Invalid difficulty selected.";
        }
        if (isBlankOrTooLong(content, 2000)) {
            return "Lesson content must be between 1 and 2000 characters.";
        }
        if (isBlankOrTooLong(quizQuestion, 255)) {
            return "Quiz question must be between 1 and 255 characters.";
        }
        if (isBlankOrTooLong(quizAnswer, 255)) {
            return "Quiz answer must be between 1 and 255 characters.";
        }
        return null;
    }

    static boolean isBlankOrTooLong(String value, int maxLength) {
        if (value == null) {
            return true;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() || trimmed.length() > maxLength;
    }
}
