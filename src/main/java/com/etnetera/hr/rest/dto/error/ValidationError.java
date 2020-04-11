package com.etnetera.hr.rest.dto.error;

import java.util.Date;

/**
 * Validation error. Represents JSON response.
 *
 * @author Etnetera
 */
public class ValidationError {

    private Date timestamp;
    private String message;
    private String details;
    private String property;
    private Object invalidValue;


    public ValidationError(String message, String details, String property, Object invalidValue) {
        this.timestamp = new Date();
        this.message = message;
        this.details = details;
        this.property = property;
        this.invalidValue = invalidValue;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(Object invalidValue) {
        this.invalidValue = invalidValue;
    }
}
