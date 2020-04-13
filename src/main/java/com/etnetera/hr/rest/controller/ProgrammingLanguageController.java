package com.etnetera.hr.rest.controller;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.ProgrammingLanguageDto;
import com.etnetera.hr.rest.dto.ProgrammingLanguageLinkDto;
import com.etnetera.hr.rest.dto.assembler.ProgrammingLanguageAssembler;
import com.etnetera.hr.rest.dto.assembler.ProgrammingLanguageLinkAssembler;
import com.etnetera.hr.rest.dto.container.InputContainer;
import com.etnetera.hr.service.ProgrammingLanguageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/languages")
public class ProgrammingLanguageController extends EntityRestController {

    private final ProgrammingLanguageService service;

    private ProgrammingLanguageAssembler assembler;

    private ProgrammingLanguageLinkAssembler linkAssembler;

    @Autowired
    public ProgrammingLanguageController(ProgrammingLanguageService service,
                                         ProgrammingLanguageAssembler assembler,
                                         ProgrammingLanguageLinkAssembler linkAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.linkAssembler = linkAssembler;
    }

    @PostMapping("/language")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO create(@RequestBody
                          @Valid ProgrammingLanguageDto dto) {
        ProgrammingLanguage language = assembler.readDto(dto);
        language = service.save(language);
        return linkAssembler.writeSimpleLinkDto(language);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<ProgrammingLanguageLinkDto> create(@RequestBody
                                                       @Valid InputContainer<ProgrammingLanguageDto> container) {
        List<ProgrammingLanguageDto> dto = container.getInputs();
        Iterable<ProgrammingLanguage> frameworks = assembler.readDto(dto);
        frameworks = service.save(frameworks);
        return linkAssembler.writeDto(frameworks);
    }

    @GetMapping("/{id}")
    public ProgrammingLanguageDto readByID(@PathVariable Long id) {
        ProgrammingLanguage language = service.findById(id);
        return assembler.writeDto(language);
    }

    @GetMapping(params = {"page", "limit"})
    public Iterable<ProgrammingLanguageDto> readByPage(@RequestParam(value = "page") int page,
                                                       @RequestParam(value = "limit") int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ProgrammingLanguage> result = service.findPage(pageable);
        return assembler.writeDto(result);
    }

    @GetMapping
    public Iterable<ProgrammingLanguageDto> readAll() {
        Iterable<ProgrammingLanguage> allLanguages = service.findAll();
        return assembler.writeDto(allLanguages);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return service.count();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody
                       @Valid InputContainer<ProgrammingLanguageDto> container) {
        List<ProgrammingLanguageDto> dto = container.getInputs();
        Iterable<ProgrammingLanguage> languages = assembler.readDtoWithId(dto);
        service.update(languages);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
        service.deleteAll();
    }
}
