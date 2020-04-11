package com.etnetera.hr.rest.dto;

/**
 * Simple Data Transfer Object. Represents JSON response to HTTP POST request.
 */
public class JavascriptFrameworkLinkDto extends LinkDTO {

    private String framework;

    public JavascriptFrameworkLinkDto(String link) {
        super(link);
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }
}
