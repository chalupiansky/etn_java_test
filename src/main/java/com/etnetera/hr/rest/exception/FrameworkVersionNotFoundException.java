package com.etnetera.hr.rest.exception;

public class FrameworkVersionNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Could not find framework version with ID: ";

    public FrameworkVersionNotFoundException(Long id) {
        super(MESSAGE + id);
    }
}
