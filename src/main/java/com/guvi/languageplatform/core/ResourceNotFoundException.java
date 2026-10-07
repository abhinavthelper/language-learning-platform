package com.guvi.languageplatform.core;

/** Custom (user-defined) exception, thrown when a requested record or report does not exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}