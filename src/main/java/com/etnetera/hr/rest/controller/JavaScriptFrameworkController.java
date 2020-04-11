package com.etnetera.hr.rest.controller;

import javax.validation.Valid;
import java.util.List;

import com.etnetera.hr.service.JavaScriptFrameworkService;
import com.etnetera.hr.rest.dto.JavaScriptFrameworkDto;
import com.etnetera.hr.rest.dto.JavascriptFrameworkLinkDto;
import com.etnetera.hr.rest.dto.LinkDTO;
import com.etnetera.hr.rest.dto.assembler.JavaScriptFrameworkAssembler;
import com.etnetera.hr.rest.dto.assembler.JavaScriptFrameworkLinkAssembler;
import com.etnetera.hr.data.entity.JavaScriptFramework;

import com.etnetera.hr.rest.dto.container.InputContainer;
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
@RequestMapping("/frameworks")
public class JavaScriptFrameworkController extends JavaScriptRestController {


    private final JavaScriptFrameworkService service;

    private JavaScriptFrameworkAssembler assembler;

    private JavaScriptFrameworkLinkAssembler linkAssembler;

    @Autowired
    public JavaScriptFrameworkController(JavaScriptFrameworkService service,
                                         JavaScriptFrameworkAssembler assembler,
                                         JavaScriptFrameworkLinkAssembler linkAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.linkAssembler = linkAssembler;
    }

    @PostMapping("/framework")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO create(@RequestBody
                          @Valid JavaScriptFrameworkDto dto) {
        JavaScriptFramework framework = assembler.readDto(dto);
        framework = service.save(framework);
        return linkAssembler.writeSimpleLinkDto(framework);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Iterable<JavascriptFrameworkLinkDto> create(@RequestBody
                                                       @Valid InputContainer<JavaScriptFrameworkDto> container) {
        List<JavaScriptFrameworkDto> dto = container.getInputs();
        Iterable<JavaScriptFramework> frameworks = assembler.readDto(dto);
        frameworks = service.saveAll(frameworks);
        return linkAssembler.writeDto(frameworks);
    }

    @GetMapping("/{id}")
    public JavaScriptFrameworkDto readByID(@PathVariable Long id) {
        JavaScriptFramework framework = service.findById(id);
        return assembler.writeDto(framework);
    }

    @GetMapping(params = {"page", "limit"})
    public Iterable<JavaScriptFrameworkDto> readByPage(@RequestParam(value = "page") int page,
                                                       @RequestParam(value = "limit") int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<JavaScriptFramework> result = service.findPage(pageable);
        return assembler.writeDto(result);
    }

    @GetMapping
    public Iterable<JavaScriptFrameworkDto> readAll() {
        Iterable<JavaScriptFramework> allFrameworks = service.findAll();
        return assembler.writeDto(allFrameworks);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return service.count();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody
                       @Valid InputContainer<JavaScriptFrameworkDto> container) {
        List<JavaScriptFrameworkDto> dto = container.getInputs();
        Iterable<JavaScriptFramework> frameworks = assembler.readDtoWithId(dto);
        service.updateAll(frameworks);
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