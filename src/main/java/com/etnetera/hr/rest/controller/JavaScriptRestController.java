package com.etnetera.hr.rest.controller;

import com.etnetera.hr.rest.dto.error.SimpleError;
import com.etnetera.hr.rest.exception.FrameworkNotFoundException;
import com.etnetera.hr.rest.exception.FrameworkVersionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class JavaScriptRestController extends EtnRestController {

    public static final String NO_SUCH_ENTITY_MESSAGE = "Entity does not exists";

    @ExceptionHandler({FrameworkNotFoundException.class, FrameworkVersionNotFoundException.class})
    public ResponseEntity<SimpleError> handleException(RuntimeException ex) {
        SimpleError error = new SimpleError(NO_SUCH_ENTITY_MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(error);
    }
}
