package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.JavascriptFrameworkLinkDto;
import com.etnetera.hr.data.entity.JavaScriptFramework;
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
public class JavaScriptFrameworkLinkAssembler {

    private String resourcesURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                             .replacePath("/frameworks/")
                                                             .toUriString();

    public LinkDTO writeSimpleLinkDto(JavaScriptFramework framework) {
        return new LinkDTO(resourcesURL + framework.getId());
    }

    public JavascriptFrameworkLinkDto writeDto(JavaScriptFramework framework) {
        String link = resourcesURL + framework.getId();
        JavascriptFrameworkLinkDto dto = new JavascriptFrameworkLinkDto(link);
        dto.setFramework(framework.getName());
        return dto;
    }

    public Iterable<JavascriptFrameworkLinkDto> writeDto(Iterable<JavaScriptFramework> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
