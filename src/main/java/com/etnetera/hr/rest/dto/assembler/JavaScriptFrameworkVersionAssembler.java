package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.rest.dto.JavaScriptFrameworkVersionDto;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;
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
public class JavaScriptFrameworkVersionAssembler {

    public JavaScriptFrameworkVersion readDto(JavaScriptFrameworkVersionDto dto) {
        JavaScriptFrameworkVersion version = new JavaScriptFrameworkVersion();
        version.setDeprecationDate(dto.getDeprecationDate());
        version.setHypeLevel(dto.getHypeLevel());
        version.setName(dto.getName());
        version.setFramework(dto.getFramework());
        return version;
    }

    public JavaScriptFrameworkVersion readDto(JavaScriptFrameworkVersionDto dto, JavaScriptFramework framework) {
        JavaScriptFrameworkVersion version = readDto(dto);
        version.setFramework(framework);
        return version;
    }

    public Iterable<JavaScriptFrameworkVersion> readDto(Iterable<JavaScriptFrameworkVersionDto> dto,
                                                        JavaScriptFramework framework) {
        return CollectionUtil.mapAll(dto, d -> readDto(d, framework));
    }

    public JavaScriptFrameworkVersion readDtoWithId(JavaScriptFrameworkVersionDto dto) {
        JavaScriptFrameworkVersion version = readDto(dto);
        version.setId(dto.getId());
        return version;
    }

    public Iterable<JavaScriptFrameworkVersion> readDtoWithId(Iterable<JavaScriptFrameworkVersionDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDtoWithId);
    }

    public JavaScriptFrameworkVersionDto writeDto(JavaScriptFrameworkVersion version) {
        JavaScriptFrameworkVersionDto dto = new JavaScriptFrameworkVersionDto();
        dto.setId(version.getId());
        dto.setName(version.getName());
        dto.setHypeLevel(version.getHypeLevel());
        dto.setDeprecationDate(version.getDeprecationDate());
        dto.setFrameworkId(version.getFramework().getId());
        return dto;
    }

    public Iterable<JavaScriptFrameworkVersionDto> writeDto(Iterable<JavaScriptFrameworkVersion> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
