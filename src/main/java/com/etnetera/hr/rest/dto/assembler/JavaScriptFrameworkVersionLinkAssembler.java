package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.JavascriptFrameworkVersionLinkDto;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;
import com.etnetera.hr.rest.dto.LinkDTO;
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
public class JavaScriptFrameworkVersionLinkAssembler {

    private String resourcesURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                             .replacePath("/frameworks/versions/")
                                                             .toUriString();

    public LinkDTO writeSimpleLinkDto(JavaScriptFrameworkVersion version) {
        return new LinkDTO(resourcesURL + version.getId());
    }

    public Iterable<JavascriptFrameworkVersionLinkDto> writeDto(Iterable<JavaScriptFrameworkVersion> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }

    public JavascriptFrameworkVersionLinkDto writeDto(JavaScriptFrameworkVersion version) {
        String link = resourcesURL + version.getId();
        JavascriptFrameworkVersionLinkDto dto = new JavascriptFrameworkVersionLinkDto(link);
        dto.setFrameworkId(version.getFramework().getId());
        dto.setVersionName(version.getName());
        return dto;
    }
}
