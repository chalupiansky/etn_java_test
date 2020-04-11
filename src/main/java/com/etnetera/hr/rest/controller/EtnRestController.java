package com.etnetera.hr.rest.controller;

import static com.etnetera.hr.util.ThrowableUtil.getRootCause;

import java.util.*;
import java.util.stream.Collectors;

import com.etnetera.hr.rest.dto.container.ErrorsContainer;
import com.etnetera.hr.rest.dto.error.SimpleError;
import com.etnetera.hr.rest.dto.error.ValidationError;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;


/**
 * Main REST controller.
 *
 * @author Etnetera
 */
public abstract class EtnRestController {

    public static final String ILLEGAL_ARGUMENT_MESSAGE = "Illegal argument exception";
    public static final String INTEGRITY_VIOLATION_MESSAGE = "Integrity violation exception";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsContainer<ValidationError>> handleException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<ValidationError> errorList = result.getFieldErrors()
                                                .stream()
                                                .map(this::mapToValidationError)
                                                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
                             .body(new ErrorsContainer<>(errorList));
    }

    private ValidationError mapToValidationError(FieldError fieldError) {
        return new ValidationError(fieldError.getCode(),
                                   fieldError.getDefaultMessage(),
                                   fieldError.getField(),
                                   fieldError.getRejectedValue());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<SimpleError> handleException(DataIntegrityViolationException ex) {
        SimpleError error = new SimpleError(INTEGRITY_VIOLATION_MESSAGE, getRootCause(ex).getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleError> handleException(IllegalArgumentException ex) {
        SimpleError error = new SimpleError(ILLEGAL_ARGUMENT_MESSAGE, getRootCause(ex).getMessage());
        return ResponseEntity.badRequest()
                             .body(error);
    }
}
