package com.etnetera.hr.rest.exception;

public class FrameworkNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Could not find framework with ID: ";

    public FrameworkNotFoundException(Long id) {
        super(MESSAGE + id);
    }
}
