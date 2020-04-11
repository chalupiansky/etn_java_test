package com.etnetera.hr.rest.dto;

/**
 * Simple Data Transfer Object. Represents JSON response to HTTP POST request.
 */
public class LinkDTO {

    private String link;

    public LinkDTO(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
