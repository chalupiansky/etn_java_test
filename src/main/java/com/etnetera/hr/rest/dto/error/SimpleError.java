package com.etnetera.hr.rest.dto.error;

import java.util.Date;

/**
 * Simple error. Represents JSON response.
 */
public class SimpleError {

    private Date timestamp;
    private String message;
    private String details;

    public SimpleError(String message, String details) {
        this.timestamp = new Date();
        this.message = message;
        this.details = details;
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
}
