package com.etnetera.hr.rest.dto;

import com.etnetera.hr.data.entity.Framework;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Simple Data Transfer Object. Represents JSON request/response.
 */
public class FrameworkVersionDto {

    public static final String NAME_EMPTY_MESSAGE = "Property 'name' can not be empty";
    public static final String NAME_MAX_LENGTH_MESSAGE = "Property 'name' cannot be longer than 30 characters";
    public static final String HYPE_LEVEL_MIN_CONST_MESSAGE = "Property 'hypeLevel' has to be greater or equal to 0";
    public static final String HYPE_LEVEL_MAX_CONST_MESSAGE = "Property 'hypeLevel' has to be lower or equal to 100";

    private Long id;

    @NotEmpty(message = NAME_EMPTY_MESSAGE)
    @Size(max = 15, message = NAME_MAX_LENGTH_MESSAGE)
    private String name;

    private Date deprecationDate;

    @Min(value = 0, message = HYPE_LEVEL_MIN_CONST_MESSAGE)
    @Max(value = 100, message = HYPE_LEVEL_MAX_CONST_MESSAGE)
    private int hypeLevel;

    private Long frameworkId;

    @JsonIgnore
    private Framework framework;

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

    public Date getDeprecationDate() {
        return deprecationDate;
    }

    public void setDeprecationDate(Date deprecationDate) {
        this.deprecationDate = deprecationDate;
    }

    public int getHypeLevel() {
        return hypeLevel;
    }

    public void setHypeLevel(int hypeLevel) {
        this.hypeLevel = hypeLevel;
    }

    public Long getFrameworkId() {
        return frameworkId;
    }

    public void setFrameworkId(Long frameworkId) {
        this.frameworkId = frameworkId;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }
}
