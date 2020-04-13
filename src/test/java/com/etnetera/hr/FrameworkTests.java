package com.etnetera.hr;


import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.etnetera.hr.data.entity.FrameworkVersion;
import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.data.repository.FrameworkRepository;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.data.repository.ProgrammingLanguageRepository;
import com.etnetera.hr.rest.controller.EtnRestController;
import com.etnetera.hr.rest.controller.EntityRestController;
import com.etnetera.hr.rest.dto.FrameworkDto;
import com.etnetera.hr.rest.dto.container.InputContainer;
import com.etnetera.hr.rest.exception.FrameworkNotFoundException;
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

import org.hamcrest.core.IsNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Class used for Spring Boot/MVC based tests.
 *
 * @author Etnetera
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FrameworkTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FrameworkRepository frameworkRepository;

    @Autowired
    private FrameworkVersionRepository versionRepository;

    @Autowired
    private ProgrammingLanguageRepository languageRepository;

    @Test
    public void addValidFrameworkTest() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        FrameworkDto frameworkDto = prepareDataForPostRequestTest(languages.get(0)).get(0);
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.link", endsWith("/frameworks/" + (1 + languages.size()))));
    }

    @Test
    public void addValidFrameworksTest() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        List<FrameworkDto> frameworkDto = prepareDataForPostRequestTest(languages.get(0));
        ResultActions actions = mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks")
                                                        .content(mapToJson(wrapData(frameworkDto)))
                                                        .contentType(MediaType.APPLICATION_JSON))
                                       .andExpect(status().isCreated())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(frameworkDto.size())));
        for (int i = 0; i < frameworkDto.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].framework", is(frameworkDto.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].link", endsWith("/languages/frameworks/" + (i + 1 + languages.size()))));
        }
    }

    @Test
    public void addInvalidFrameworkViolatingUniqueName() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        FrameworkDto frameworkDto = prepareDataForPostRequestTest(languages.get(0)).get(0);
        List<FrameworkDto> frameworksWithSameName = Arrays.asList(frameworkDto, frameworkDto);
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks")
                                .content(mapToJson(wrapData(frameworksWithSameName)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message", is(EtnRestController.INTEGRITY_VIOLATION_MESSAGE)));
    }

    @Test
    public void addInvalidFrameworkWithTooLongName() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        FrameworkDto frameworkDto = new FrameworkDto();
        frameworkDto.setName("verylongnameofthejavascriptframeworkjavaisthebest");
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is("verylongnameofthejavascriptframeworkjavaisthebest")));
    }

    @Test
    public void addInvalidFrameworkWithoutName() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        FrameworkDto frameworkDto = new FrameworkDto();
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkDto.NAME_EMPTY_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue").value(IsNull.nullValue()));
    }

    @Test
    public void addInvalidFrameworksWithoutRequestBody() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidFrameworkWithoutRequestBody() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks/framework"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidFrameworkWithEmptyRequestBody() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        List<FrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks")
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
    public void getAllFrameworks() throws Exception {
        List<Framework> frameworks = prepareData();
        ResultActions actions = mockMvc.perform(get("/languages/frameworks"))
                                       .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(frameworks.size())));
        for (int i = 0; i < frameworks.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(frameworks.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(frameworks.get(i).getName())));
        }
    }

    @Test
    public void getAllFrameworksByLanguage() throws Exception {
        prepareData();
        Long languageId = 1L;
        List<Framework> languageFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(languageId));
        ResultActions actions = mockMvc.perform(get("/languages/" + languageId + "/frameworks"))
                                       .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(languageFrameworks.size())));
        for (int i = 0; i < languageFrameworks.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(languageFrameworks.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(languageFrameworks.get(i).getName())));
        }
    }

    @Test
    public void getExistingFramework() throws Exception {
        List<Framework> frameworks = prepareData();
        Framework framework = frameworks.get(0);
        int existingId = framework.getId().intValue();
        mockMvc.perform(get("/languages/frameworks/" + existingId))
               .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("id", is(existingId)))
               .andExpect(jsonPath("name", is(framework.getName())));
    }

    @Test
    public void getNonExistentFramework() throws Exception {
        List<Framework> frameworks = prepareData();
        int nonExistentId = frameworks.size() + 100;
        mockMvc.perform(get("/languages/frameworks/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void getFrameworksByPageAndLimitParams() throws Exception {
        prepareData();
        Long languageId = 1L;
        List<Framework> languageFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(languageId));
        List<Framework> firstPageData = languageFrameworks.subList(0, 2);
        List<Framework> secondPageData = languageFrameworks.subList(2, 4);

        ResultActions actions1 = mockMvc.perform(get("/languages/1/frameworks?page=0&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(firstPageData.size())));
        for (int i = 0; i < firstPageData.size(); i++) {
            actions1.andExpect(jsonPath("$[" + i + "].id", is(firstPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(firstPageData.get(i).getName())));
        }

        ResultActions actions2 = mockMvc.perform(get("/languages/1/frameworks?page=1&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(secondPageData.size())));
        for (int i = 0; i < secondPageData.size(); i++) {
            actions2.andExpect(jsonPath("$[" + i + "].id", is(secondPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(secondPageData.get(i).getName())));
        }
    }

    @Test
    public void getFrameworksByWrongPageParam() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        int wrongPageParam = -1;
        mockMvc.perform(get("/languages/" + languages.get(0).getId().intValue() + "/frameworks?page=" + wrongPageParam + "&limit=2"))
               .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.message", is(EtnRestController.ILLEGAL_ARGUMENT_MESSAGE)))
               .andExpect(jsonPath("$.details", is("Page index must not be less than zero!")));
    }

    @Test
    public void getFrameworkCount() throws Exception {
        MvcResult resultBeforePersist = mockMvc.perform(get("/languages/frameworks/count"))
                                               .andExpect(status().isOk())
                                               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                               .andReturn();
        Assert.assertEquals(String.valueOf(0), resultBeforePersist.getResponse().getContentAsString());

        List<Framework> frameworks = prepareData();
        MvcResult resultAfterPersist = mockMvc.perform(get("/languages/frameworks/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(frameworks.size()), resultAfterPersist.getResponse().getContentAsString());
    }


    @Test
    public void getFrameworkCountByLanguage() throws Exception {
        prepareData();
        Long languageId = 1L;
        List<Framework> languageFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(languageId));
        MvcResult resultAfterPersist = mockMvc.perform(get("/languages/" + languageId.intValue() + "/frameworks/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(languageFrameworks.size()), resultAfterPersist.getResponse().getContentAsString());
    }

    @Test
    public void updateFramework() throws Exception {
        List<Framework> frameworks = prepareData();
        frameworks.forEach(f -> f.setName("x" + f.getName()));

        mockMvc.perform(put("/languages/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        List<Framework> frameworksFromDB = CollectionUtil.mapToList(frameworkRepository.findAll());

        for (int i = 0; i < frameworksFromDB.size(); i++) {
            Assert.assertEquals(frameworks.get(i).getName(), frameworksFromDB.get(i).getName());
        }
    }

    @Test
    public void updateFrameworkWithoutId() throws Exception {
        List<Framework> frameworks = prepareData();
        frameworks.get(0).setId(null);

        mockMvc.perform(put("/languages/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Illegal argument exception")))
               .andExpect(jsonPath("$.details", is("The given id must not be null!")));
    }

    @Test
    public void updateOfNonExistentFramework() throws Exception {
        List<Framework> frameworks = prepareData();
        long nonExistentId = 100;
        frameworks.get(0).setId(nonExistentId);

        mockMvc.perform(put("/languages/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void updateFrameworkWithInvalidContent() throws Exception {
        List<Framework> frameworks = prepareData();
        String invalidName = "verylongnameofthejavascriptframeworkjavaisthebest";
        frameworks.get(0).setName(invalidName);
        mockMvc.perform(put("/languages/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(FrameworkDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidName)));
    }

    @Test
    public void updateFrameworksWithoutRequestBody() throws Exception {
        mockMvc.perform(put("/languages/frameworks")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFrameworksWithEmptyRequestBody() throws Exception {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();
        List<FrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/languages/" + languages.get(0).getId().intValue() + "/frameworks")
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
    public void deleteFrameworkById() throws Exception {
        List<Framework> frameworks = prepareData();
        Long id = frameworks.get(0).getId();
        mockMvc.perform(delete("/languages/frameworks/" + id))
               .andExpect(status().isOk());
        List<Framework> updatedFrameworks = CollectionUtil.mapToList(frameworkRepository.findAll());
        boolean frameworkFound = updatedFrameworks.stream().anyMatch(f -> f.getId().equals(id));

        List<FrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findByFramework(frameworks.get(0)));
        boolean emptyVersions = updatedVersions.isEmpty();

        Assert.assertFalse(frameworkFound);
        Assert.assertTrue(emptyVersions);
    }

    @Test
    public void deleteNonExistentFramework() throws Exception {
        int nonExistentId = 100;
        mockMvc.perform(delete("/languages/frameworks/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void deleteAllByLanguage() throws Exception {
        prepareData();
        Long languageId = 1L;
        List<Framework> languageFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(languageId));

        mockMvc.perform(delete("/languages/" + languageId.intValue() + "/frameworks"))
               .andExpect(status().isOk());

        List<Framework> updatedFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(languageId));
        Assert.assertTrue(updatedFrameworks.isEmpty());

        languageFrameworks.forEach(f -> {
            List<FrameworkVersion> versions = CollectionUtil.mapToList(versionRepository.findByFramework(f));
            Assert.assertTrue(versions.isEmpty());
        });
    }

    @Test
    public void deleteAllFrameworks() throws Exception {
        prepareData();
        mockMvc.perform(delete("/languages/frameworks"))
               .andExpect(status().isOk());

        List<Framework> updatedFrameworks = CollectionUtil.mapToList(frameworkRepository.findAll());
        boolean emptyFrameworks = updatedFrameworks.isEmpty();


        List<FrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findAll());
        boolean emptyVersions = updatedVersions.isEmpty();

        Assert.assertTrue(emptyFrameworks);
        Assert.assertTrue(emptyVersions);

    }

    private List<ProgrammingLanguage> prepareProgrammingLanguages() {
        List<ProgrammingLanguage> languages = new ArrayList<>();

        ProgrammingLanguage javascript = new ProgrammingLanguage();
        javascript.setName("JavaScript");

        ProgrammingLanguage java = new ProgrammingLanguage();
        java.setName("Java");

        languages.add(javascript);
        languages.add(java);

        languageRepository.saveAll(languages);

        return languages;
    }

    private List<Framework> prepareData() {
        List<ProgrammingLanguage> languages = prepareProgrammingLanguages();

        List<Framework> frameworks = new ArrayList<>();
        Framework react = new Framework();
        react.setName("React");
        react.setLanguage(languages.get(0));

        Framework vue = new Framework();
        vue.setName("Vue.js");
        vue.setLanguage(languages.get(0));

        Framework angular = new Framework();
        angular.setName("Angular");
        angular.setLanguage(languages.get(0));

        Framework node = new Framework();
        node.setName("Node.js");
        node.setLanguage(languages.get(0));

        Framework spring = new Framework();
        spring.setName("Spring");
        spring.setLanguage(languages.get(1));

        frameworks.add(react);
        frameworks.add(vue);
        frameworks.add(angular);
        frameworks.add(node);
        frameworks.add(spring);

        frameworkRepository.saveAll(frameworks);
        prepareFrameworksVersions(frameworks);

        return frameworks;
    }

    private void prepareFrameworksVersions(List<Framework> frameworks) {

        FrameworkVersion version1 = new FrameworkVersion();
        version1.setDeprecationDate(new Date());
        version1.setHypeLevel(75);
        version1.setName("1.1");
        version1.setFramework(frameworks.get(0));

        FrameworkVersion version2 = new FrameworkVersion();
        version2.setDeprecationDate(new Date());
        version2.setHypeLevel(64);
        version2.setName("1.2");
        version2.setFramework(frameworks.get(1));

        FrameworkVersion version3 = new FrameworkVersion();
        version3.setDeprecationDate(new Date());
        version3.setHypeLevel(17);
        version3.setName("1.3");
        version3.setFramework(frameworks.get(3));

        versionRepository.save(version1);
        versionRepository.save(version2);
        versionRepository.save(version3);
    }

    private List<FrameworkDto> prepareDataForPostRequestTest(ProgrammingLanguage language) {
        List<FrameworkDto> testData = new ArrayList<>();
        FrameworkDto dto1 = new FrameworkDto();
        dto1.setName("Angular");
        dto1.setLanguageId(language.getId());
        FrameworkDto dto2 = new FrameworkDto();
        dto2.setName("Vue.Js");
        dto2.setLanguageId(language.getId());
        FrameworkDto dto3 = new FrameworkDto();
        dto3.setName("React");
        dto3.setLanguageId(language.getId());
        testData.add(dto1);
        testData.add(dto2);
        testData.add(dto3);
        return testData;
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
