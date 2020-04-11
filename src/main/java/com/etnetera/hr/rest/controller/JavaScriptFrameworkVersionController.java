package com.etnetera.hr.rest.controller;

import javax.validation.Valid;
import java.util.List;

import com.etnetera.hr.service.JavaScriptFrameworkService;
import com.etnetera.hr.service.JavaScriptFrameworkVersionService;
import com.etnetera.hr.rest.dto.JavaScriptFrameworkVersionDto;
import com.etnetera.hr.rest.dto.JavascriptFrameworkVersionLinkDto;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.assembler.JavaScriptFrameworkVersionAssembler;
import com.etnetera.hr.rest.dto.assembler.JavaScriptFrameworkVersionLinkAssembler;
import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;
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
@RequestMapping("/frameworks")
public class JavaScriptFrameworkVersionController  extends JavaScriptRestController {

    private final JavaScriptFrameworkVersionService versionService;
    private final JavaScriptFrameworkService frameworkService;

    private JavaScriptFrameworkVersionAssembler versionAssembler;
    private JavaScriptFrameworkVersionLinkAssembler linkAssembler;

    @Autowired
    public JavaScriptFrameworkVersionController(JavaScriptFrameworkVersionService versionService,
                                                JavaScriptFrameworkService frameworkService,
                                                JavaScriptFrameworkVersionAssembler assembler,
                                                JavaScriptFrameworkVersionLinkAssembler linkAssembler) {
        this.versionService = versionService;
        this.frameworkService = frameworkService;
        this.versionAssembler = assembler;
        this.linkAssembler = linkAssembler;
    }

    @PostMapping("/{id}/version")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO create(@RequestBody @Valid JavaScriptFrameworkVersionDto dto,
                          @PathVariable Long id) {
        JavaScriptFramework framework = frameworkService.getOne(id);
        JavaScriptFrameworkVersion version = versionAssembler.readDto(dto, framework);
        version = versionService.save(version);
        return linkAssembler.writeSimpleLinkDto(version);
    }

    @PostMapping("/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<JavascriptFrameworkVersionLinkDto> create(@RequestBody
                                                              @Valid InputContainer<JavaScriptFrameworkVersionDto> container,
                                                              @PathVariable Long id) {
        JavaScriptFramework framework = frameworkService.getOne(id);
        Iterable<JavaScriptFrameworkVersion> versions = versionAssembler.readDto(container.getInputs(), framework);
        versions = versionService.saveAll(versions);
        return linkAssembler.writeDto(versions);
    }

    @GetMapping("/versions/{id}")
    public JavaScriptFrameworkVersionDto read(@PathVariable Long id) {
        JavaScriptFrameworkVersion version = versionService.findById(id);
        return versionAssembler.writeDto(version);
    }

    @GetMapping("/{id}/versions")
    public Iterable<JavaScriptFrameworkVersionDto> readByFramework(@PathVariable Long id) {
        JavaScriptFramework framework = frameworkService.findById(id);
        Iterable<JavaScriptFrameworkVersion> frameworks = versionService.findByFramework(framework);
        return versionAssembler.writeDto(frameworks);
    }

    @GetMapping(path = "/{id}/versions", params = {"page", "limit"})
    public Iterable<JavaScriptFrameworkVersionDto> readByPage(@RequestParam("page") int page,
                                                              @RequestParam("limit") int limit,
                                                              @PathVariable Long id) {
        JavaScriptFramework framework = frameworkService.findById(id);
        Pageable pageable = PageRequest.of(page, limit);
        Page<JavaScriptFrameworkVersion> result = versionService.findByFramework(framework, pageable);
        return versionAssembler.writeDto(result);
    }

    @GetMapping("/versions")
    public Iterable<JavaScriptFrameworkVersionDto> readAll() {
        Iterable<JavaScriptFrameworkVersion> frameworks = versionService.findAll();
        return versionAssembler.writeDto(frameworks);
    }

    @GetMapping("/versions/count")
    public Integer countAll() {
        return versionService.count();
    }

    @GetMapping("{id}/versions/count")
    public Integer countForFramework(@PathVariable Long id) {
        JavaScriptFramework framework = frameworkService.findById(id);
        return versionService.countByFramework(framework);
    }

    @PutMapping("/versions")
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody
                       @Valid InputContainer<JavaScriptFrameworkVersionDto> container) {
        List<JavaScriptFrameworkVersionDto> dto = container.getInputs();
        Iterable<JavaScriptFrameworkVersion> versions = versionAssembler.readDtoWithId(dto);
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
        JavaScriptFramework framework = frameworkService.getOne(id);
        versionService.deleteAllByFramework(framework);
    }

    @DeleteMapping("/versions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByFramework() {
        versionService.deleteAll();
    }
}