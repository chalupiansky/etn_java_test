package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.rest.dto.ProgrammingLanguageDto;
import com.etnetera.hr.util.CollectionUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Component for mapping Data Transfer Objects from REST API
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProgrammingLanguageAssembler {

    public ProgrammingLanguage readDto(ProgrammingLanguageDto dto) {
        ProgrammingLanguage framework = new ProgrammingLanguage();
        framework.setName(dto.getName());
        return framework;
    }

    public Iterable<ProgrammingLanguage> readDto(Iterable<ProgrammingLanguageDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDto);
    }

    public ProgrammingLanguage readDtoWithId(ProgrammingLanguageDto dto) {
        ProgrammingLanguage framework = readDto(dto);
        framework.setId(dto.getId());
        return framework;
    }

    public Iterable<ProgrammingLanguage> readDtoWithId(Iterable<ProgrammingLanguageDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDtoWithId);
    }

    public ProgrammingLanguageDto writeDto(ProgrammingLanguage language) {
        ProgrammingLanguageDto dto = new ProgrammingLanguageDto();
        dto.setName(language.getName());
        dto.setId(language.getId());
        return dto;
    }

    public Iterable<ProgrammingLanguageDto> writeDto(Iterable<ProgrammingLanguage> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
