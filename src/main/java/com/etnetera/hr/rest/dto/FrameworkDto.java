package com.etnetera.hr.rest.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Simple Data Transfer Object. Represents JSON request/response.
 */
public class FrameworkDto {

    public static final String NAME_EMPTY_MESSAGE = "Property 'name' can not be empty";
    public static final String NAME_MAX_LENGTH_MESSAGE = "Property 'name' cannot be longer than 30 characters";

    private Long id;

    @Size(max = 30, message = NAME_MAX_LENGTH_MESSAGE)
    @NotEmpty(message = NAME_EMPTY_MESSAGE)
    private String name;

    private Long languageId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }
}
