package com.etnetera.hr.rest.exception;

public class ProgrammingLanguageNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Could not find programming language with ID: ";

    public ProgrammingLanguageNotFoundException(Long id) {
        super(MESSAGE + id);
    }
}
