package com.etnetera.hr;


import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;
import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.data.repository.JavaScriptFrameworkVersionRepository;
import com.etnetera.hr.rest.controller.EtnRestController;
import com.etnetera.hr.rest.controller.JavaScriptRestController;
import com.etnetera.hr.rest.dto.JavaScriptFrameworkDto;
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
public class JavaScriptFrameworkTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JavaScriptFrameworkRepository repository;

    @Autowired
    private JavaScriptFrameworkVersionRepository versionRepository;

    @Test
    public void addValidFrameworkTest() throws Exception {
        JavaScriptFrameworkDto frameworkDto = prepareDataForPostRequestTest().get(0);
        mockMvc.perform(post("/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.link", endsWith("/frameworks/1")));
    }

    @Test
    public void addValidFrameworksTest() throws Exception {
        List<JavaScriptFrameworkDto> frameworkDto = prepareDataForPostRequestTest();
        ResultActions actions = mockMvc.perform(post("/frameworks")
                                                        .content(mapToJson(wrapData(frameworkDto)))
                                                        .contentType(MediaType.APPLICATION_JSON))
                                       .andExpect(status().isCreated())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(frameworkDto.size())));
        for (int i = 0; i < frameworkDto.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].framework", is(frameworkDto.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].link", endsWith("/frameworks/" + (i + 1))));
        }
    }

    @Test
    public void addInvalidFrameworkViolatingUniqueName() throws Exception {
        JavaScriptFrameworkDto frameworkDto = prepareDataForPostRequestTest().get(0);
        List<JavaScriptFrameworkDto> frameworksWithSameName = Arrays.asList(frameworkDto, frameworkDto);
        mockMvc.perform(post("/frameworks")
                                .content(mapToJson(wrapData(frameworksWithSameName)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message", is(EtnRestController.INTEGRITY_VIOLATION_MESSAGE)));
    }

    @Test
    public void addInvalidFrameworkWithTooLongName() throws Exception {
        JavaScriptFrameworkDto frameworkDto = new JavaScriptFrameworkDto();
        frameworkDto.setName("verylongnameofthejavascriptframeworkjavaisthebest");
        mockMvc.perform(post("/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(JavaScriptFrameworkDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is("verylongnameofthejavascriptframeworkjavaisthebest")));
    }

    @Test
    public void addInvalidFrameworkWithoutName() throws Exception {
        JavaScriptFrameworkDto frameworkDto = new JavaScriptFrameworkDto();
        mockMvc.perform(post("/frameworks/framework")
                                .content(mapToJson(frameworkDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")))
               .andExpect(jsonPath("$.errors[0].details", is(JavaScriptFrameworkDto.NAME_EMPTY_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue").value(IsNull.nullValue()));
    }

    @Test
    public void addInvalidFrameworksWithoutRequestBody() throws Exception {
        mockMvc.perform(post("/frameworks"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidFrameworkWithoutRequestBody() throws Exception {
        mockMvc.perform(post("/frameworks/framework"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidFrameworkWithEmptyRequestBody() throws Exception {
        List<JavaScriptFrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/frameworks")
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
        List<JavaScriptFramework> frameworks = prepareData();
        ResultActions actions = mockMvc.perform(get("/frameworks"))
                                       .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(frameworks.size())));
        for (int i = 0; i < frameworks.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(frameworks.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(frameworks.get(i).getName())));
        }
    }

    @Test
    public void getExistingFramework() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        JavaScriptFramework framework = frameworks.get(0);
        int existingId = framework.getId().intValue();
        mockMvc.perform(get("/frameworks/" + existingId))
               .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("id", is(existingId)))
               .andExpect(jsonPath("name", is(framework.getName())));
    }

    @Test
    public void getNonExistentFramework() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        int nonExistentId = frameworks.size() + 1;
        mockMvc.perform(get("/frameworks/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(JavaScriptRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void getFrameworksByPageAndLimitParams() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        List<JavaScriptFramework> firstPageData = frameworks.subList(0, 2);
        List<JavaScriptFramework> secondPageData = frameworks.subList(2, 4);

        ResultActions actions1 = mockMvc.perform(get("/frameworks?page=0&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(firstPageData.size())));
        for (int i = 0; i < firstPageData.size(); i++) {
            actions1.andExpect(jsonPath("$[" + i + "].id", is(firstPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(firstPageData.get(i).getName())));
        }

        ResultActions actions2 = mockMvc.perform(get("/frameworks?page=1&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(secondPageData.size())));
        for (int i = 0; i < secondPageData.size(); i++) {
            actions2.andExpect(jsonPath("$[" + i + "].id", is(secondPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(secondPageData.get(i).getName())));
        }
    }

    @Test
    public void getFrameworksByWrongPageParam() throws Exception {
        int wrongPageParam = -1;
        mockMvc.perform(get("/frameworks?page=" + wrongPageParam + "&limit=2"))
               .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.message", is(EtnRestController.ILLEGAL_ARGUMENT_MESSAGE)))
               .andExpect(jsonPath("$.details", is("Page index must not be less than zero!")));
    }

    @Test
    public void getFrameworkCount() throws Exception {
        MvcResult resultBeforePersist = mockMvc.perform(get("/frameworks/count"))
                                               .andExpect(status().isOk())
                                               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                               .andReturn();
        Assert.assertEquals(String.valueOf(0), resultBeforePersist.getResponse().getContentAsString());

        List<JavaScriptFramework> frameworks = prepareData();
        MvcResult resultAfterPersist = mockMvc.perform(get("/frameworks/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(frameworks.size()), resultAfterPersist.getResponse().getContentAsString());
    }

    @Test
    public void updateFramework() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        frameworks.forEach(f -> f.setName("x" + f.getName()));

        mockMvc.perform(put("/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        List<JavaScriptFramework> frameworksFromDB = CollectionUtil.mapToList(repository.findAll());

        for (int i = 0; i < frameworksFromDB.size(); i++) {
            Assert.assertEquals(frameworks.get(i).getName(), frameworksFromDB.get(i).getName());
        }
    }

    @Test
    public void updateFrameworkWithoutId() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        frameworks.get(0).setId(null);

        mockMvc.perform(put("/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Illegal argument exception")))
               .andExpect(jsonPath("$.details", is("The given id must not be null!")));
    }

    @Test
    public void updateOfNonExistentFramework() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        long nonExistentId = 100;
        frameworks.get(0).setId(nonExistentId);

        mockMvc.perform(put("/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(JavaScriptRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void updateFrameworkWithInvalidContent() throws Exception {
        List<JavaScriptFramework> frameworks = prepareData();
        String invalidName = "verylongnameofthejavascriptframeworkjavaisthebest";
        frameworks.get(0).setName(invalidName);
        mockMvc.perform(put("/frameworks")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(JavaScriptFrameworkDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidName)));
    }

    @Test
    public void updateFrameworksWithoutRequestBody() throws Exception {
        mockMvc.perform(put("/frameworks")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFrameworksWithEmptyRequestBody() throws Exception {
        List<JavaScriptFrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/frameworks")
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
        List<JavaScriptFramework> frameworks = prepareData();
        Long id = frameworks.get(0).getId();
        mockMvc.perform(delete("/frameworks/" + id))
               .andExpect(status().isOk());
        List<JavaScriptFramework> updatedFrameworks = CollectionUtil.mapToList(repository.findAll());
        boolean frameworkFound = updatedFrameworks.stream().anyMatch(f -> f.getId().equals(id));

        List<JavaScriptFrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findByFramework(frameworks.get(0)));
        boolean emptyVersions = updatedVersions.isEmpty();

        Assert.assertFalse(frameworkFound);
        Assert.assertTrue(emptyVersions);
    }

    @Test
    public void deleteNonExistentFramework() throws Exception {
        int nonExistentId = 100;
        mockMvc.perform(delete("/frameworks/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(JavaScriptRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(FrameworkNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void deleteAllFrameworks() throws Exception {
        prepareData();
        mockMvc.perform(delete("/frameworks"))
               .andExpect(status().isOk());

        List<JavaScriptFramework> updatedFrameworks = CollectionUtil.mapToList(repository.findAll());
        boolean emptyFrameworks = updatedFrameworks.isEmpty();


        List<JavaScriptFrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findAll());
        boolean emptyVersions = updatedVersions.isEmpty();

        Assert.assertTrue(emptyFrameworks);
        Assert.assertTrue(emptyVersions);

    }

    private List<JavaScriptFramework> prepareData() {
        List<JavaScriptFramework> frameworks = new ArrayList<>();
        JavaScriptFramework react = new JavaScriptFramework();
        react.setName("React");
        JavaScriptFramework vue = new JavaScriptFramework();
        vue.setName("Vue.js");
        JavaScriptFramework angular = new JavaScriptFramework();
        angular.setName("Angular");
        JavaScriptFramework node = new JavaScriptFramework();
        node.setName("Node.js");
        JavaScriptFramework backbone = new JavaScriptFramework();
        backbone.setName("Backbone.js");
        frameworks.add(react);
        frameworks.add(vue);
        frameworks.add(angular);
        frameworks.add(node);
        frameworks.add(backbone);

        repository.saveAll(frameworks);
        prepareFrameworksVersions(frameworks);

        return frameworks;
    }

    private void prepareFrameworksVersions(List<JavaScriptFramework> frameworks) {
        JavaScriptFrameworkVersion version1 = new JavaScriptFrameworkVersion();
        version1.setDeprecationDate(new Date());
        version1.setHypeLevel(75);
        version1.setName("1.1");
        version1.setFramework(frameworks.get(0));

        JavaScriptFrameworkVersion version2 = new JavaScriptFrameworkVersion();
        version2.setDeprecationDate(new Date());
        version2.setHypeLevel(64);
        version2.setName("1.2");
        version2.setFramework(frameworks.get(1));

        JavaScriptFrameworkVersion version3 = new JavaScriptFrameworkVersion();
        version3.setDeprecationDate(new Date());
        version3.setHypeLevel(17);
        version3.setName("1.3");
        version3.setFramework(frameworks.get(3));

        versionRepository.save(version1);
        versionRepository.save(version2);
        versionRepository.save(version3);
    }

    private List<JavaScriptFrameworkDto> prepareDataForPostRequestTest() {
        List<JavaScriptFrameworkDto> testData = new ArrayList<>();
        JavaScriptFrameworkDto dto1 = new JavaScriptFrameworkDto();
        dto1.setName("Angular");
        JavaScriptFrameworkDto dto2 = new JavaScriptFrameworkDto();
        dto2.setName("Vue.JS");
        JavaScriptFrameworkDto dto3 = new JavaScriptFrameworkDto();
        dto3.setName("React");
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
