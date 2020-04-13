package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.FrameworkDto;
import com.etnetera.hr.data.entity.Framework;
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
public class FrameworkAssembler {

    public Framework readDto(FrameworkDto dto) {
        Framework framework = new Framework();
        framework.setName(dto.getName());
        return framework;
    }

    public Iterable<Framework> readDto(Iterable<FrameworkDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDto);
    }

    public Framework readDtoWithId(FrameworkDto dto) {
        Framework framework = readDto(dto);
        framework.setId(dto.getId());
        return framework;
    }

    public Iterable<Framework> readDtoWithId(Iterable<FrameworkDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDtoWithId);
    }

    public FrameworkDto writeDto(Framework framework) {
        FrameworkDto dto = new FrameworkDto();
        dto.setName(framework.getName());
        dto.setLanguageId(framework.getLanguage().getId());
        dto.setId(framework.getId());
        return dto;
    }

    public Iterable<FrameworkDto> writeDto(Iterable<Framework> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
