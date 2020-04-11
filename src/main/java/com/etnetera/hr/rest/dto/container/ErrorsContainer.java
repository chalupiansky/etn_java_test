package com.etnetera.hr.rest.dto.container;

import java.util.List;

/**
 *
 * Envelope for the errors. Represents JSON response.
 *
 * @author Etnetera
 *
 */
public class ErrorsContainer<T> {

    private List<T> errors;

    public ErrorsContainer(List<T> errors) {
        this.errors = errors;
    }

    public List<T> getErrors() {
        return errors;
    }

    public void setErrors(List<T> errors) {
        this.errors = errors;
    }

}
