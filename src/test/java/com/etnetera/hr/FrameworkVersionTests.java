package com.etnetera.hr;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;
import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.data.repository.FrameworkRepository;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.data.repository.ProgrammingLanguageRepository;
import com.etnetera.hr.rest.controller.EtnRestController;
import com.etnetera.hr.rest.controller.EntityRestController;
import com.etnetera.hr.rest.dto.FrameworkDto;
import com.etnetera.hr.rest.dto.FrameworkVersionDto;
import com.etnetera.hr.rest.dto.assembler.FrameworkVersionAssembler;
import com.etnetera.hr.rest.dto.container.InputContainer;
import com.etnetera.hr.rest.exception.FrameworkNotFoundException;
import com.etnetera.hr.rest.exception.FrameworkVersionNotFoundException;
import com.etnetera.hr.util.CollectionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class used for Spring Boot/MVC based tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FrameworkVersionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FrameworkRepository frameworkRepository;

    @Autowired
    private FrameworkVersionRepository versionRepository;

    @Autowired
    private FrameworkVersionAssembler versionAssembler;

    @Autowired
    private ProgrammingLanguageRepository languageRepository;

    @Test
    public void addValidVersion() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        Framework forFramework = frameworks.get(0);
        List<FrameworkVersionDto> versionDto = prepareFrameworkVersionDto(forFramework);

        ResultActions actions = mockMvc.perform(post("/languages/frameworks/" + forFramework.getId().intValue() + "/versions")
                                                        .content(mapToJson(wrapData(versionDto)))
                                                        .contentType(MediaType.APPLICATION_JSON))
                                       .andExpect(status().isCreated())
                                       .andExpect(jsonPath("$", hasSize(versionDto.size())));
        for (int i = 0; i < versionDto.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].link", endsWith("/languages/frameworks/versions/" + (frameworks.size() + i + 1 + 1))))
                   .andExpect(jsonPath("$[" + i + "].versionName", is(versionDto.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].frameworkId", is(forFramework.getId().intValue())));
        }
    }

    @Test
    public void addDuplicatedVersion() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        Framework forFramework = frameworks.get(0);
        List<FrameworkVersionDto> versionDto = prepareFrameworkVersionDto(forFramework);
        List<FrameworkVersionDto> duplicateVersionDto = Arrays.asList(versionDto.get(0), versionDto.get(0));

        mockMvc.perform(post("/languages/frameworks/" + forFramework.getId().intValue() + "/versions")
                                .content(mapToJson(wrapData(duplicateVersionDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message", is(EtnRestController.INTEGRITY_VIOLATION_MESSAGE)));
    }

    @Test
    public void addVersionsToNonExistentFramework() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        Framework forFramework = frameworks.get(0);
        List<FrameworkVersionDto> versionDto = prepareFrameworkVersionDto(forFramework);
        int nonExistentFrameworkId = 1000;

        mockMvc.perform(post("/languages/frameworks/" + nonExistentFrameworkId + "/versions")
                                .content(mapToJson(wrapData(versionDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentFrameworkId)));
    }

    @Test
    public void addVersionsWithInvalidHypeLevel() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        Framework framework = frameworks.get(0);

        List<FrameworkVersionDto> versionDto = prepareFrameworkVersionDto(framework);
        int invalidHypeLevel = 101;
        versionDto.get(0).setHypeLevel(invalidHypeLevel);

        mockMvc.perform(post("/languages/frameworks/" + framework.getId().intValue() + "/versions")
                                .content(mapToJson(wrapData(versionDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Max")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkVersionDto.HYPE_LEVEL_MAX_CONST_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].hypeLevel")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidHypeLevel)));
    }

    @Test
    public void addVersionsWithInvalidVersion() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        Framework framework = frameworks.get(0);

        List<FrameworkVersionDto> versionDto = prepareFrameworkVersionDto(framework);
        String invalidVersion = "TooLongVersionName";
        versionDto.get(0).setName(invalidVersion);

        mockMvc.perform(post("/languages/frameworks/" + framework.getId().intValue() + "/versions")
                                .content(mapToJson(wrapData(versionDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkVersionDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidVersion)));
    }

    @Test
    public void addInvalidVersionsWithoutRequestBody() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        mockMvc.perform(post("/languages/frameworks/"  + frameworks.get(0).getId().intValue() + "/versions"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidVersionWithoutRequestBody() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        mockMvc.perform(post("/languages/frameworks/" + frameworks.get(0).getId().intValue() + "/versions"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidVersionWithEmptyRequestBody() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        List<FrameworkVersionDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/languages/frameworks/" + frameworks.get(0).getId().intValue() + "/versions")
                                .content(mapToJson(wrapData(emptyList)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors", hasSize(1)))
               .andExpect(jsonPath("$.errors[0].details", is(InputContainer.EMPTY_INPUT_LIST_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")))
               .andExpect(jsonPath("$.errors[0].property", is("inputs")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(new ArrayList())));
    }

    @Test
    public void getAllVersions() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();

        ResultActions actions = mockMvc.perform(get("/languages/frameworks/versions"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(preparedVersions.size())));
        for (int i = 0; i < preparedVersions.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(preparedVersions.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(preparedVersions.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].hypeLevel", is(preparedVersions.get(i).getHypeLevel())))
                   .andExpect(jsonPath("$[" + i + "].frameworkId", is(preparedVersions.get(i).getFramework().getId().intValue())));
        }
    }

    @Test
    public void getVersionsByExistingFramework() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        Framework forFramework = preparedVersions.get(0).getFramework();
        List<FrameworkVersion> expectedVersions = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));
        int frameworkId = forFramework.getId().intValue();

        ResultActions actions = mockMvc.perform(get("/languages/frameworks/" + frameworkId + "/versions"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(expectedVersions.size())));
        for (int i = 0; i < expectedVersions.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(expectedVersions.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(expectedVersions.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].hypeLevel", is(expectedVersions.get(i).getHypeLevel())))
                   .andExpect(jsonPath("$[" + i + "].frameworkId", is(frameworkId)));
        }
    }

    @Test
    public void getVersionsByNonExistentFramework() throws Exception {
        ProgrammingLanguage language = prepareLanguage("JavaScript");
        List<Framework> frameworks = prepareFrameworks(language);
        int nonExistentId = frameworks.size() + 100;
        mockMvc.perform(get("/languages/frameworks/" + nonExistentId + "/versions"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void getVersionsByPageAndLimitParams() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        Framework forFramework = preparedVersions.get(0).getFramework();
        List<FrameworkVersion> versionsOfFramework = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));

        List<FrameworkVersion> firstPageData = versionsOfFramework.subList(0, 2);
        List<FrameworkVersion> secondPageData = versionsOfFramework.subList(2, 4);

        ResultActions actions1 = mockMvc.perform(get("/languages/frameworks/" + forFramework.getId().intValue() + "/versions?page=0&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(firstPageData.size())));
        for (int i = 0; i < firstPageData.size(); i++) {
            actions1.andExpect(jsonPath("$[" + i + "].id", is(firstPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(firstPageData.get(i).getName())))
                    .andExpect(jsonPath("$[" + i + "].hypeLevel", is(firstPageData.get(i).getHypeLevel())))
                    .andExpect(jsonPath("$[" + i + "].frameworkId", is(forFramework.getId().intValue())));
        }

        ResultActions actions2 = mockMvc.perform(get("/languages/frameworks/" + forFramework.getId().intValue() + "/versions?page=1&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(secondPageData.size())));

        for (int i = 0; i < secondPageData.size(); i++) {
            actions2.andExpect(jsonPath("$[" + i + "].id", is(secondPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(secondPageData.get(i).getName())))
                    .andExpect(jsonPath("$[" + i + "].hypeLevel", is(secondPageData.get(i).getHypeLevel())))
                    .andExpect(jsonPath("$[" + i + "].frameworkId", is(forFramework.getId().intValue())));
        }
    }

    @Test
    public void getFrameworksByWrongPageParam() throws Exception {
        prepareFrameworkVersions();
        int wrongPageParam = -1;
        mockMvc.perform(get("/languages/frameworks/" + 2 + "/versions?page=" + wrongPageParam + "&limit=2"))
               .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.message", is(EtnRestController.ILLEGAL_ARGUMENT_MESSAGE)))
               .andExpect(jsonPath("$.details", is("Page index must not be less than zero!")));
    }

    @Test
    public void getVersionsCount() throws Exception {
        MvcResult resultBeforePersist = mockMvc.perform(get("/languages/frameworks/versions/count"))
                                               .andExpect(status().isOk())
                                               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                               .andReturn();
        Assert.assertEquals(String.valueOf(0), resultBeforePersist.getResponse().getContentAsString());

        List<FrameworkVersion> versions = prepareFrameworkVersions();
        MvcResult resultAfterPersist = mockMvc.perform(get("/languages/frameworks/versions/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(versions.size()), resultAfterPersist.getResponse().getContentAsString());
    }

    @Test
    public void getVersionsCountOfFramework() throws Exception {
        List<FrameworkVersion> versions = prepareFrameworkVersions();
        Framework forFramework = versions.get(0).getFramework();
        List<FrameworkVersion> frameworkVersions = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));

        MvcResult resultAfterPersist = mockMvc.perform(get("/languages/frameworks/" + forFramework.getId().intValue() + "/versions/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(frameworkVersions.size()), resultAfterPersist.getResponse().getContentAsString());
    }


    @Test
    public void updateVersions() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        Framework forFramework = preparedVersions.get(0).getFramework();
        List<FrameworkVersion> versionsOfFramework = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));
        List<FrameworkVersionDto> versionOfFrameworkDto = CollectionUtil.mapToList(versionAssembler.writeDto(versionsOfFramework));
        versionOfFrameworkDto.forEach(dto -> dto.setFrameworkId(forFramework.getId()));

        mockMvc.perform(put("/languages/frameworks/versions")
                                .content(mapToJson(wrapData(versionOfFrameworkDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        List<FrameworkVersion> versionsFromDB = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));

        for (int i = 0; i < versionsFromDB.size(); i++) {
            Assert.assertEquals(versionOfFrameworkDto.get(i).getName(), versionsFromDB.get(i).getName());
        }
    }


    @Test
    public void updateVersionsWithoutId() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        preparedVersions.get(0).setId(null);

        mockMvc.perform(put("/languages/frameworks/versions")
                                .content(mapToJson(wrapData(preparedVersions)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Illegal argument exception")))
               .andExpect(jsonPath("$.details", is("The given id must not be null!")));
    }

    @Test
    public void updateOfNonExistentVersion() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        Framework forFramework = preparedVersions.get(0).getFramework();
        List<FrameworkVersion> versionsOfFramework = CollectionUtil.mapToList(versionRepository.findByFramework(forFramework));
        List<FrameworkVersionDto> versionOfFrameworkDto = CollectionUtil.mapToList(versionAssembler.writeDto(versionsOfFramework));
        versionOfFrameworkDto.forEach(dto -> dto.setFrameworkId(forFramework.getId()));
        long id = 100;
        versionOfFrameworkDto.get(0).setId(id);

        mockMvc.perform(put("/languages/frameworks/versions")
                                .content(mapToJson(wrapData(versionOfFrameworkDto)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkVersionNotFoundException.MESSAGE + id)));
    }

    @Test
    public void updateVersionsWithInvalidContent() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        String invalidVersion = "TooLongVersionName";
        preparedVersions.get(0).setName(invalidVersion);
        mockMvc.perform(put("/languages/frameworks/versions")
                                .content(mapToJson(wrapData(preparedVersions)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkVersionDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidVersion)));
    }


    @Test
    public void updateVersionsWithoutRequestBody() throws Exception {
        mockMvc.perform(put("/languages/frameworks/versions")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void updateVersionsWithEmptyRequestBody() throws Exception {
        List<FrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(put("/languages/frameworks/versions")
                                .content(mapToJson(wrapData(emptyList)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors", hasSize(1)))
               .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")))
               .andExpect(jsonPath("$.errors[0].details", is(InputContainer.EMPTY_INPUT_LIST_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(new ArrayList())));
    }

    @Test
    public void deleteExistingVersion() throws Exception {
        List<FrameworkVersion> preparedVersions = prepareFrameworkVersions();
        Long id = preparedVersions.get(0).getId();
        mockMvc.perform(delete("/languages/frameworks/versions/" + id))
               .andExpect(status().isOk());

        List<FrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findAll());
        boolean versionFound = updatedVersions.stream().anyMatch(f -> f.getId().equals(id));

        Assert.assertFalse(versionFound);
    }

    @Test
    public void deleteNonExistentVersion() throws Exception {
        int nonExistentId = 100;
        mockMvc.perform(delete("/languages/frameworks/versions/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkVersionNotFoundException.MESSAGE + nonExistentId)));
    }
    
    private ProgrammingLanguage prepareLanguage(String name) {
        ProgrammingLanguage javascript = new ProgrammingLanguage();
        javascript.setName(name);
        languageRepository.save(javascript);
        return javascript;
    }


    private List<Framework> prepareFrameworks(ProgrammingLanguage language) {
        List<Framework> frameworks = new ArrayList<>();
        Framework react = new Framework();
        react.setName("React");
        react.setLanguage(language);
        Framework vue = new Framework();
        vue.setName("Vue.js");
        vue.setLanguage(language);
        Framework angular = new Framework();
        angular.setName("Angular");
        angular.setLanguage(language);
        Framework node = new Framework();
        node.setName("Node.js");
        node.setLanguage(language);
        Framework backbone = new Framework();
        backbone.setName("Backbone.js");
        backbone.setLanguage(language);
        frameworks.add(react);
        frameworks.add(vue);
        frameworks.add(angular);
        frameworks.add(node);
        frameworks.add(backbone);
        frameworkRepository.saveAll(frameworks);
        return frameworks;
    }


    private List<FrameworkVersion> prepareFrameworkVersions() {
        ProgrammingLanguage language = prepareLanguage("JavaScripy");
        List<Framework> frameworks = prepareFrameworks(language);
        List<FrameworkVersion> versions = new ArrayList<>();

        FrameworkVersion version1 = new FrameworkVersion();
        version1.setFramework(frameworks.get(0));
        version1.setName("1.1");
        version1.setHypeLevel(10);
        version1.setDeprecationDate(new Date());

        FrameworkVersion version2 = new FrameworkVersion();
        version2.setFramework(frameworks.get(0));
        version2.setName("1.2");
        version2.setHypeLevel(15);
        version2.setDeprecationDate(new Date());

        FrameworkVersion version3 = new FrameworkVersion();
        version3.setFramework(frameworks.get(0));
        version3.setName("1.3");
        version3.setHypeLevel(19);
        version3.setDeprecationDate(new Date());

        FrameworkVersion version4 = new FrameworkVersion();
        version4.setFramework(frameworks.get(0));
        version4.setName("1.4");
        version4.setHypeLevel(25);
        version4.setDeprecationDate(new Date());

        FrameworkVersion version5 = new FrameworkVersion();
        version5.setFramework(frameworks.get(1));
        version5.setName("1.1");
        version5.setHypeLevel(13);
        version5.setDeprecationDate(new Date());

        FrameworkVersion version6 = new FrameworkVersion();
        version6.setFramework(frameworks.get(2));
        version6.setName("1.1");
        version6.setHypeLevel(73);
        version6.setDeprecationDate(new Date());

        versions.add(version1);
        versions.add(version2);
        versions.add(version3);
        versions.add(version4);
        versions.add(version5);
        versions.add(version6);

        versionRepository.saveAll(versions);

        return versions;
    }

    private List<FrameworkVersionDto> prepareFrameworkVersionDto(Framework framework) {
        FrameworkVersionDto versionDto1 = new FrameworkVersionDto();
        versionDto1.setDeprecationDate(new Date());
        versionDto1.setHypeLevel(75);
        versionDto1.setName("1.1");
        versionDto1.setFrameworkId(framework.getId());
        versionDto1.setFramework(framework);

        FrameworkVersionDto versionDto2 = new FrameworkVersionDto();
        versionDto2.setDeprecationDate(new Date());
        versionDto2.setHypeLevel(64);
        versionDto2.setName("1.2");
        versionDto2.setFrameworkId(framework.getId());
        versionDto2.setFramework(framework);

        FrameworkVersionDto versionDto3 = new FrameworkVersionDto();
        versionDto3.setDeprecationDate(new Date());
        versionDto3.setHypeLevel(17);
        versionDto3.setName("1.3");
        versionDto3.setFrameworkId(framework.getId());
        versionDto3.setFramework(framework);

        List<FrameworkVersionDto> versionDto = new ArrayList<>();
        versionDto.add(versionDto1);
        versionDto.add(versionDto2);
        versionDto.add(versionDto3);

        return versionDto;
    }

    public <T> InputContainer<T> wrapData(List<T> list) {
        InputContainer<T> container = new InputContainer<>();
        container.setInputs(list);
        return container;
    }

    public static byte[] mapToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
