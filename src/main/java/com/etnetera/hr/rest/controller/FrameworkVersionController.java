package com.etnetera.hr.rest.controller;

import javax.validation.Valid;
import java.util.List;

import com.etnetera.hr.service.FrameworkService;
import com.etnetera.hr.service.FrameworkVersionService;
import com.etnetera.hr.rest.dto.FrameworkVersionDto;
import com.etnetera.hr.rest.dto.FrameworkVersionLinkDto;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.assembler.FrameworkVersionAssembler;
import com.etnetera.hr.rest.dto.assembler.FrameworkVersionLinkAssembler;
import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;
import com.etnetera.hr.rest.dto.container.InputContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/languages/frameworks")
public class FrameworkVersionController extends EntityRestController {

    private final FrameworkVersionService versionService;
    private final FrameworkService frameworkService;

    private FrameworkVersionAssembler versionAssembler;
    private FrameworkVersionLinkAssembler linkAssembler;

    @Autowired
    public FrameworkVersionController(FrameworkVersionService versionService,
                                      FrameworkService frameworkService,
                                      FrameworkVersionAssembler assembler,
                                      FrameworkVersionLinkAssembler linkAssembler) {
        this.versionService = versionService;
        this.frameworkService = frameworkService;
        this.versionAssembler = assembler;
        this.linkAssembler = linkAssembler;
    }

    @PostMapping("/{id}/versions/version")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO create(@RequestBody @Valid FrameworkVersionDto dto,
                          @PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        FrameworkVersion version = versionAssembler.readDto(dto, framework);
        version = versionService.save(version);
        return linkAssembler.writeSimpleLinkDto(version);
    }

    @PostMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<FrameworkVersionLinkDto> create(@RequestBody
                                                              @Valid InputContainer<FrameworkVersionDto> container,
                                                    @PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        Iterable<FrameworkVersion> versions = versionAssembler.readDto(container.getInputs(), framework);
        versions = versionService.saveAll(versions);
        return linkAssembler.writeDto(versions);
    }

    @GetMapping("/versions/{id}")
    public FrameworkVersionDto read(@PathVariable Long id) {
        FrameworkVersion version = versionService.findById(id);
        return versionAssembler.writeDto(version);
    }

    @GetMapping("/{id}/versions")
    public Iterable<FrameworkVersionDto> readByFramework(@PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        Iterable<FrameworkVersion> frameworks = versionService.findByFramework(framework);
        return versionAssembler.writeDto(frameworks);
    }

    @GetMapping(path = "/{id}/versions", params = {"page", "limit"})
    public Iterable<FrameworkVersionDto> readByPage(@RequestParam("page") int page,
                                                    @RequestParam("limit") int limit,
                                                    @PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        Pageable pageable = PageRequest.of(page, limit);
        Page<FrameworkVersion> result = versionService.findByFramework(framework, pageable);
        return versionAssembler.writeDto(result);
    }

    @GetMapping("/versions")
    public Iterable<FrameworkVersionDto> readAll() {
        Iterable<FrameworkVersion> frameworks = versionService.findAll();
        return versionAssembler.writeDto(frameworks);
    }

    @GetMapping("/versions/count")
    public Integer countAll() {
        return versionService.count();
    }

    @GetMapping("/{id}/versions/count")
    public Integer countForFramework(@PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        return versionService.countByFramework(framework);
    }

    @PutMapping("/versions")
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody
                       @Valid InputContainer<FrameworkVersionDto> container) {
        List<FrameworkVersionDto> dto = container.getInputs();
        Iterable<FrameworkVersion> versions = versionAssembler.readDtoWithId(dto);
        versionService.updateAll(versions);
    }

    @DeleteMapping("/versions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        versionService.deleteById(id);
    }

    @DeleteMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByFramework(@PathVariable Long id) {
        Framework framework = frameworkService.getOne(id);
        versionService.deleteAllByFramework(framework);
    }

    @DeleteMapping("/versions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByFramework() {
        versionService.deleteAll();
    }
}