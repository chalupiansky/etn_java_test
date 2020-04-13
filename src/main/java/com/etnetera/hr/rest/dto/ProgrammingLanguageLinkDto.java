package com.etnetera.hr.rest.dto;

public class ProgrammingLanguageLinkDto extends LinkDTO {

    private String programmingLanguage;

    public ProgrammingLanguageLinkDto(String link) {
        super(link);
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }
}
