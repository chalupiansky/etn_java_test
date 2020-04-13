package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.FrameworkLinkDto;
import com.etnetera.hr.data.entity.Framework;
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
public class FrameworkLinkAssembler {

    private String resourcesURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                             .replacePath("/languages/frameworks/")
                                                             .toUriString();

    public LinkDTO writeSimpleLinkDto(Framework framework) {
        return new LinkDTO(resourcesURL + framework.getId());
    }

    public FrameworkLinkDto writeDto(Framework framework) {
        String link = resourcesURL + framework.getId();
        FrameworkLinkDto dto = new FrameworkLinkDto(link);
        dto.setFramework(framework.getName());
        return dto;
    }

    public Iterable<FrameworkLinkDto> writeDto(Iterable<Framework> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }
}
