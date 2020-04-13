package com.etnetera.hr.rest.controller;

import javax.validation.Valid;
import java.util.List;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.service.FrameworkService;
import com.etnetera.hr.rest.dto.FrameworkDto;
import com.etnetera.hr.rest.dto.FrameworkLinkDto;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.assembler.FrameworkAssembler;
import com.etnetera.hr.rest.dto.assembler.FrameworkLinkAssembler;
import com.etnetera.hr.data.entity.Framework;

import com.etnetera.hr.rest.dto.container.InputContainer;
import com.etnetera.hr.service.ProgrammingLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


/**
 * Simple REST controller for accessing application logic.
 *
 * @author Etnetera
 */
@RestController
@RequestMapping("/languages")
public class FrameworkController extends EntityRestController {


    private final FrameworkService frameworkService;

    private final ProgrammingLanguageService languageService;

    private FrameworkAssembler assembler;

    private FrameworkLinkAssembler linkAssembler;

    @Autowired
    public FrameworkController(FrameworkService service,
                               ProgrammingLanguageService languageService,
                               FrameworkAssembler assembler,
                               FrameworkLinkAssembler linkAssembler) {
        this.frameworkService = service;
        this.languageService = languageService;
        this.assembler = assembler;
        this.linkAssembler = linkAssembler;
    }

    @PostMapping("/{id}/frameworks/framework")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO create(@RequestBody
                          @Valid FrameworkDto dto,
                          @PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        Framework framework = assembler.readDto(dto);
        framework.setLanguage(language);
        framework = frameworkService.save(framework);
        return linkAssembler.writeSimpleLinkDto(framework);
    }

    @PostMapping("/{id}/frameworks")
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<FrameworkLinkDto> create(@RequestBody
                                                       @Valid InputContainer<FrameworkDto> container,
                                             @PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        List<FrameworkDto> dto = container.getInputs();
        Iterable<Framework> frameworks = assembler.readDto(dto);
        frameworks.forEach(f -> f.setLanguage(language));
        frameworks = frameworkService.saveAll(frameworks);
        return linkAssembler.writeDto(frameworks);
    }

    @GetMapping("/frameworks/{id}")
    public FrameworkDto readByID(@PathVariable Long id) {
        Framework framework = frameworkService.findById(id);
        return assembler.writeDto(framework);
    }

    @GetMapping("/{id}/frameworks")
    public Iterable<FrameworkDto> readByLanguage(@PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        Iterable<Framework> frameworks = frameworkService.findByLanguage(language);
        return assembler.writeDto(frameworks);
    }

    @GetMapping(path = "/{id}/frameworks", params = {"page", "limit"})
    public Iterable<FrameworkDto> readByPage(@RequestParam(value = "page") int page,
                                             @RequestParam(value = "limit") int limit,
                                             @PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        Pageable pageable = PageRequest.of(page, limit);
        Page<Framework> result = frameworkService.findByLanguage(language, pageable);
        return assembler.writeDto(result);
    }

    @GetMapping("/frameworks")
    public Iterable<FrameworkDto> readAll() {
        Iterable<Framework> allFrameworks = frameworkService.findAll();
        return assembler.writeDto(allFrameworks);
    }

    @GetMapping("/frameworks/count")
    public Integer getCount() {
        return frameworkService.count();
    }

    @GetMapping("/{id}/frameworks/count")
    public Integer countForFramework(@PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        return frameworkService.countByLanguage(language);
    }

    @PutMapping("/frameworks")
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody
                       @Valid InputContainer<FrameworkDto> container) {
        List<FrameworkDto> dto = container.getInputs();
        Iterable<Framework> frameworks = assembler.readDtoWithId(dto);
        frameworkService.updateAll(frameworks);
    }

    @DeleteMapping("/frameworks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        frameworkService.deleteById(id);
    }

    @DeleteMapping("/{id}/frameworks")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByFramework(@PathVariable Long id) {
        ProgrammingLanguage language = languageService.getOne(id);
        frameworkService.deleteAllByLanguage(language);
    }

    @DeleteMapping("/frameworks")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
        frameworkService.deleteAll();
    }
}