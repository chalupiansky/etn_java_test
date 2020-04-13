package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.ProgrammingLanguageLinkDto;
import com.etnetera.hr.util.CollectionUtil;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Component for mapping Data Transfer Objects from REST API
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProgrammingLanguageLinkAssembler {

    private String resourcesURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                             .replacePath("/languages/")
                                                             .toUriString();

    public LinkDTO writeSimpleLinkDto(ProgrammingLanguage language) {
        return new LinkDTO(resourcesURL + language.getId());
    }

    public ProgrammingLanguageLinkDto writeDto(ProgrammingLanguage language) {
        String link = resourcesURL + language.getId();
        ProgrammingLanguageLinkDto dto = new ProgrammingLanguageLinkDto(link);
        dto.setProgrammingLanguage(language.getName());
        return dto;
    }

    public Iterable<ProgrammingLanguageLinkDto> writeDto(Iterable<ProgrammingLanguage> languages) {
        return CollectionUtil.mapAll(languages, this::writeDto);
    }
}
