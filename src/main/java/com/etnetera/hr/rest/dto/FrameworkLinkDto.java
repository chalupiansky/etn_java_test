package com.etnetera.hr.rest.dto;

/**
 * Simple Data Transfer Object. Represents JSON response to HTTP POST request.
 */
public class FrameworkLinkDto extends LinkDTO {

    private String framework;

    public FrameworkLinkDto(String link) {
        super(link);
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }
}
