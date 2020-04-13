package com.etnetera.hr.rest.dto.assembler;

import com.etnetera.hr.rest.dto.FrameworkVersionLinkDto;
import com.etnetera.hr.data.entity.FrameworkVersion;
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
public class FrameworkVersionLinkAssembler {

    private String resourcesURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                             .replacePath("/languages/frameworks/versions/")
                                                             .toUriString();

    public LinkDTO writeSimpleLinkDto(FrameworkVersion version) {
        return new LinkDTO(resourcesURL + version.getId());
    }

    public Iterable<FrameworkVersionLinkDto> writeDto(Iterable<FrameworkVersion> versions) {
        return CollectionUtil.mapAll(versions, this::writeDto);
    }

    public FrameworkVersionLinkDto writeDto(FrameworkVersion version) {
        String link = resourcesURL + version.getId();
        FrameworkVersionLinkDto dto = new FrameworkVersionLinkDto(link);
        dto.setFrameworkId(version.getFramework().getId());
        dto.setVersionName(version.getName());
        return dto;
    }
}
