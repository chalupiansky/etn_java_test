package com.etnetera.hr;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;
import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.data.repository.FrameworkRepository;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.data.repository.ProgrammingLanguageRepository;
import com.etnetera.hr.rest.controller.EntityRestController;
import com.etnetera.hr.rest.controller.EtnRestController;
import com.etnetera.hr.rest.dto.FrameworkDto;
import com.etnetera.hr.rest.dto.ProgrammingLanguageDto;
import com.etnetera.hr.rest.dto.container.InputContainer;
import com.etnetera.hr.rest.exception.ProgrammingLanguageNotFoundException;
import com.etnetera.hr.util.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class used for Spring Boot/MVC based tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProgrammingLanguageTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FrameworkRepository frameworkRepository;

    @Autowired
    private FrameworkVersionRepository versionRepository;

    @Autowired
    private ProgrammingLanguageRepository languageRepository;


    @Test
    public void addValidLanguageTest() throws Exception {
        ProgrammingLanguageDto languageDto = prepareDataForPostRequestTest().get(0);
        mockMvc.perform(post("/languages/language")
                                .content(mapToJson(languageDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.link", endsWith("/languages/1")));
    }


    @Test
    public void addValidLanguagesTest() throws Exception {
        List<ProgrammingLanguageDto> languageDto = prepareDataForPostRequestTest();
        ResultActions actions = mockMvc.perform(post("/languages")
                                                        .content(mapToJson(wrapData(languageDto)))
                                                        .contentType(MediaType.APPLICATION_JSON))
                                       .andExpect(status().isCreated())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(languageDto.size())));
        for (int i = 0; i < languageDto.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].programmingLanguage", is(languageDto.get(i).getName())))
                   .andExpect(jsonPath("$[" + i + "].link", endsWith("/languages/" + (i + 1))));
        }
    }

    @Test
    public void addInvalidLanguagesViolatingUniqueName() throws Exception {
        ProgrammingLanguageDto languageDto = prepareDataForPostRequestTest().get(0);
        List<ProgrammingLanguageDto> frameworksWithSameName = Arrays.asList(languageDto, languageDto);
        mockMvc.perform(post("/languages")
                                .content(mapToJson(wrapData(frameworksWithSameName)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message", is(EtnRestController.INTEGRITY_VIOLATION_MESSAGE)));
    }


    @Test
    public void addInvalidLanguageWithTooLongName() throws Exception {
        ProgrammingLanguageDto languageDto = new ProgrammingLanguageDto();
        String tooLongName = "verylongnameofthelanguagenamejavaisthebest";
        languageDto.setName(tooLongName);
        mockMvc.perform(post("/languages/language")
                                .content(mapToJson(languageDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(ProgrammingLanguageDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(tooLongName)));
    }

    @Test
    public void addInvalidLanguageWithoutName() throws Exception {
        ProgrammingLanguageDto languageDto = new ProgrammingLanguageDto();
        mockMvc.perform(post("/languages/language")
                                .content(mapToJson(languageDto))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")))
               .andExpect(jsonPath("$.errors[0].details", is(ProgrammingLanguageDto.NAME_EMPTY_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("name")))
               .andExpect(jsonPath("$.errors[0].invalidValue").value(IsNull.nullValue()));
    }

    @Test
    public void addInvalidLanguagesWithoutRequestBody() throws Exception {
        mockMvc.perform(post("/languages"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidLanguageWithoutRequestBody() throws Exception {
        mockMvc.perform(post("/languages/language"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void addInvalidLanguageWithEmptyRequestBody() throws Exception {
        List<ProgrammingLanguageDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/languages")
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
    public void getAllLanguages() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        ResultActions actions = mockMvc.perform(get("/languages"))
                                       .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                       .andExpect(jsonPath("$", hasSize(languages.size())));
        for (int i = 0; i < languages.size(); i++) {
            actions.andExpect(jsonPath("$[" + i + "].id", is(languages.get(i).getId().intValue())))
                   .andExpect(jsonPath("$[" + i + "].name", is(languages.get(i).getName())));
        }
    }

    @Test
    public void getExistingLanguage() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        ProgrammingLanguage language = languages.get(0);
        int existingId = language.getId().intValue();
        mockMvc.perform(get("/languages/" + existingId))
               .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("id", is(existingId)))
               .andExpect(jsonPath("name", is(language.getName())));
    }

    @Test
    public void getNonExistentLanguage() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        int nonExistentId = languages.size() + 1;
        mockMvc.perform(get("/languages/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(ProgrammingLanguageNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void getLanguagesByPageAndLimitParams() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        List<ProgrammingLanguage> firstPageData = languages.subList(0, 2);
        List<ProgrammingLanguage> secondPageData = languages.subList(2, 4);

        ResultActions actions1 = mockMvc.perform(get("/languages?page=0&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(firstPageData.size())));
        for (int i = 0; i < firstPageData.size(); i++) {
            actions1.andExpect(jsonPath("$[" + i + "].id", is(firstPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(firstPageData.get(i).getName())));
        }

        ResultActions actions2 = mockMvc.perform(get("/languages?page=1&limit=2"))
                                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$", hasSize(secondPageData.size())));
        for (int i = 0; i < secondPageData.size(); i++) {
            actions2.andExpect(jsonPath("$[" + i + "].id", is(secondPageData.get(i).getId().intValue())))
                    .andExpect(jsonPath("$[" + i + "].name", is(secondPageData.get(i).getName())));
        }
    }

    @Test
    public void getLanguagesByWrongPageParam() throws Exception {
        int wrongPageParam = -1;
        mockMvc.perform(get("/languages?page=" + wrongPageParam + "&limit=2"))
               .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.message", is(EtnRestController.ILLEGAL_ARGUMENT_MESSAGE)))
               .andExpect(jsonPath("$.details", is("Page index must not be less than zero!")));
    }

    @Test
    public void getLanguageCount() throws Exception {
        MvcResult resultBeforePersist = mockMvc.perform(get("/languages/count"))
                                               .andExpect(status().isOk())
                                               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                               .andReturn();
        Assert.assertEquals(String.valueOf(0), resultBeforePersist.getResponse().getContentAsString());

        List<ProgrammingLanguage> languages = prepareData();
        MvcResult resultAfterPersist = mockMvc.perform(get("/languages/count"))
                                              .andExpect(status().isOk())
                                              .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                              .andReturn();
        Assert.assertEquals(String.valueOf(languages.size()), resultAfterPersist.getResponse().getContentAsString());
    }

    @Test
    public void updateLanguage() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        languages.forEach(f -> f.setName("x" + f.getName()));

        mockMvc.perform(put("/languages")
                                .content(mapToJson(wrapData(languages)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        List<ProgrammingLanguage> languagesFromDb = CollectionUtil.mapToList(languageRepository.findAll());

        for (int i = 0; i < languagesFromDb.size(); i++) {
            Assert.assertEquals(languages.get(i).getName(), languagesFromDb.get(i).getName());
        }
    }

    @Test
    public void updateLanguageWithoutId() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        languages.get(0).setId(null);

        mockMvc.perform(put("/languages")
                                .content(mapToJson(wrapData(languages)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Illegal argument exception")))
               .andExpect(jsonPath("$.details", is("The given id must not be null!")));
    }

    @Test
    public void updateOfNonExistentLanguage() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        long nonExistentId = 100;
        languages.get(0).setId(nonExistentId);

        mockMvc.perform(put("/languages")
                                .content(mapToJson(wrapData(languages)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(ProgrammingLanguageNotFoundException.MESSAGE + nonExistentId)));
    }


    @Test
    public void updateLanguageWithInvalidContent() throws Exception {
        List<ProgrammingLanguage> frameworks = prepareData();
        String invalidName = "verylongnameoftheprogramminglanguagejavaisthebest";
        frameworks.get(0).setName(invalidName);
        mockMvc.perform(put("/languages")
                                .content(mapToJson(wrapData(frameworks)))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors[0].message", is("Size")))
               .andExpect(jsonPath("$.errors[0].details", is(ProgrammingLanguageDto.NAME_MAX_LENGTH_MESSAGE)))
               .andExpect(jsonPath("$.errors[0].property", is("inputs[0].name")))
               .andExpect(jsonPath("$.errors[0].invalidValue", is(invalidName)));
    }

    @Test
    public void updateLanguagesWithoutRequestBody() throws Exception {
        mockMvc.perform(put("/languages")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void updateLanguagesWithEmptyRequestBody() throws Exception {
        List<FrameworkDto> emptyList = new ArrayList<>();
        mockMvc.perform(post("/languages")
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
    public void deleteLanguageById() throws Exception {
        List<ProgrammingLanguage> languages = prepareData();
        Long id = languages.get(0).getId();

        List<Framework> languageFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(id));

        mockMvc.perform(delete("/languages/" + id))
               .andExpect(status().isOk());

        List<ProgrammingLanguage> updatedLanguages = CollectionUtil.mapToList(languageRepository.findAll());
        boolean languageFound = updatedLanguages.stream().anyMatch(l -> l.getId().equals(id));

        List<Framework> updatedFrameworks = CollectionUtil.mapToList(frameworkRepository.findByLanguage_Id(id));
        boolean frameworksFound = updatedFrameworks.stream().anyMatch(f -> f.getLanguage().equals(languages.get(0)));

        languageFrameworks.forEach(f -> {
            List<FrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findByFramework(f));
            Assert.assertTrue(updatedVersions.isEmpty());
        });

        Assert.assertFalse(languageFound);
        Assert.assertFalse(frameworksFound);
    }

    @Test
    public void deleteNonExistentLanguage() throws Exception {
        int nonExistentId = 100;
        mockMvc.perform(delete("/languages/" + nonExistentId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message", is(EntityRestController.NO_SUCH_ENTITY_MESSAGE)))
               .andExpect(jsonPath("$.details", is(ProgrammingLanguageNotFoundException.MESSAGE + nonExistentId)));
    }

    @Test
    public void deleteAllLanguages() throws Exception {
        prepareData();
        mockMvc.perform(delete("/languages"))
               .andExpect(status().isOk());

        List<ProgrammingLanguage> updatedLanguages = CollectionUtil.mapToList(languageRepository.findAll());
        boolean emptyLanguages = updatedLanguages.isEmpty();

        List<Framework> updatedFrameworks = CollectionUtil.mapToList(frameworkRepository.findAll());
        boolean emptyFrameworks = updatedFrameworks.isEmpty();


        List<FrameworkVersion> updatedVersions = CollectionUtil.mapToList(versionRepository.findAll());
        boolean emptyVersions = updatedVersions.isEmpty();

        Assert.assertTrue(emptyLanguages);
        Assert.assertTrue(emptyFrameworks);
        Assert.assertTrue(emptyVersions);
    }


    private List<ProgrammingLanguage> prepareData() {
        List<ProgrammingLanguage> languages = new ArrayList<>();

        ProgrammingLanguage javascript = new ProgrammingLanguage();
        javascript.setName("JavaScript");

        ProgrammingLanguage java = new ProgrammingLanguage();
        java.setName("Java");

        ProgrammingLanguage python = new ProgrammingLanguage();
        python.setName("Python");

        ProgrammingLanguage kotlin = new ProgrammingLanguage();
        kotlin.setName("Kotlin");


        languages.add(javascript);
        languages.add(java);
        languages.add(python);
        languages.add(kotlin);

        languageRepository.saveAll(languages);
        languageRepository.flush();

        prepareFrameworks(languages);

        return languages;
    }

    private void prepareFrameworks(List<ProgrammingLanguage> languages) {
        List<Framework> frameworks = new ArrayList<>();
        Framework framework1 = new Framework();
        framework1.setLanguage(languages.get(0));
        framework1.setName("Vue.js");

        Framework framework2 = new Framework();
        framework2.setLanguage(languages.get(0));
        framework2.setName("Angular.js");

        Framework framework3 = new Framework();
        framework3.setLanguage(languages.get(1));
        framework3.setName("Django");

        Framework framework4 = new Framework();
        framework4.setLanguage(languages.get(1));
        framework4.setName("Grok");

        frameworks.add(framework1);
        frameworks.add(framework2);
        frameworks.add(framework3);
        frameworks.add(framework4);

        frameworkRepository.saveAll(frameworks);
        frameworkRepository.flush();

        prepareFrameworksVersions(frameworks);

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

    private List<ProgrammingLanguageDto> prepareDataForPostRequestTest() {
        List<ProgrammingLanguageDto> testData = new ArrayList<>();
        ProgrammingLanguageDto dto1 = new ProgrammingLanguageDto();
        dto1.setName("JavaScript");
        ProgrammingLanguageDto dto2 = new ProgrammingLanguageDto();
        dto2.setName("Python");
        testData.add(dto1);
        testData.add(dto2);
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
