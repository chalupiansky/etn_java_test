package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.rest.dto.FrameworkVersionDto;
import com.etnetera.hr.data.entity.FrameworkVersion;
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
public class FrameworkVersionAssembler {

    public FrameworkVersion readDto(FrameworkVersionDto dto) {
        FrameworkVersion version = new FrameworkVersion();
        version.setDeprecationDate(dto.getDeprecationDate());
        version.setHypeLevel(dto.getHypeLevel());
        version.setName(dto.getName());
        version.setFramework(dto.getFramework());
        return version;
    }

    public FrameworkVersion readDto(FrameworkVersionDto dto, Framework framework) {
        FrameworkVersion version = readDto(dto);
        version.setFramework(framework);
        return version;
    }

    public Iterable<FrameworkVersion> readDto(Iterable<FrameworkVersionDto> dto,
                                              Framework framework) {
        return CollectionUtil.mapAll(dto, d -> readDto(d, framework));
    }

    public FrameworkVersion readDtoWithId(FrameworkVersionDto dto) {
        FrameworkVersion version = readDto(dto);
        version.setId(dto.getId());
        return version;
    }

    public Iterable<FrameworkVersion> readDtoWithId(Iterable<FrameworkVersionDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDtoWithId);
    }

    public FrameworkVersionDto writeDto(FrameworkVersion version) {
        FrameworkVersionDto dto = new FrameworkVersionDto();
        dto.setId(version.getId());
        dto.setName(version.getName());
        dto.setHypeLevel(version.getHypeLevel());
        dto.setDeprecationDate(version.getDeprecationDate());
        dto.setFrameworkId(version.getFramework().getId());
        return dto;
    }

    public Iterable<FrameworkVersionDto> writeDto(Iterable<FrameworkVersion> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
