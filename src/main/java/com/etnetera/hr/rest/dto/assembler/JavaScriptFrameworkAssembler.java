package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.JavaScriptFrameworkDto;
import com.etnetera.hr.data.entity.JavaScriptFramework;
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
public class JavaScriptFrameworkAssembler {

    public JavaScriptFramework readDto(JavaScriptFrameworkDto dto) {
        JavaScriptFramework framework = new JavaScriptFramework();
        framework.setName(dto.getName());
        return framework;
    }

    public Iterable<JavaScriptFramework> readDto(Iterable<JavaScriptFrameworkDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDto);
    }

    public JavaScriptFramework readDtoWithId(JavaScriptFrameworkDto dto) {
        JavaScriptFramework framework = readDto(dto);
        framework.setId(dto.getId());
        return framework;
    }

    public Iterable<JavaScriptFramework> readDtoWithId(Iterable<JavaScriptFrameworkDto> dto) {
        return CollectionUtil.mapAll(dto, this::readDtoWithId);
    }

    public JavaScriptFrameworkDto writeDto(JavaScriptFramework framework) {
        JavaScriptFrameworkDto dto = new JavaScriptFrameworkDto();
        dto.setName(framework.getName());
        dto.setId(framework.getId());
        return dto;
    }

    public Iterable<JavaScriptFrameworkDto> writeDto(Iterable<JavaScriptFramework> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
