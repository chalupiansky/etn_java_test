package com.etnetera.hr.rest.dto;

/**
 * Simple Data Transfer Object. Represents JSON response to HTTP POST request
 */
public class JavascriptFrameworkVersionLinkDto extends LinkDTO {

    private String versionName;
    private Long frameworkId;

    public JavascriptFrameworkVersionLinkDto(String link) {
        super(link);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getFrameworkId() {
        return frameworkId;
    }

    public void setFrameworkId(Long frameworkId) {
        this.frameworkId = frameworkId;
    }
}
