package com.etnetera.hr.service;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.data.repository.FrameworkRepository;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.data.repository.ProgrammingLanguagePagingRepository;
import com.etnetera.hr.data.repository.ProgrammingLanguageRepository;
import com.etnetera.hr.rest.exception.ProgrammingLanguageNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ProgrammingLanguageService {

    private final ProgrammingLanguageRepository languageRepository;
    private final ProgrammingLanguagePagingRepository pagingRepository;
    private final FrameworkRepository frameworkRepository;
    private final FrameworkVersionRepository versionRepository;
    private final FrameworkService frameworkService;

    @Autowired
    public ProgrammingLanguageService(ProgrammingLanguageRepository languageRepository,
                                      ProgrammingLanguagePagingRepository pagingRepository,
                                      FrameworkRepository frameworkRepository,
                                      FrameworkVersionRepository versionRepository,
                                      FrameworkService frameworkService) {
        this.languageRepository = languageRepository;
        this.pagingRepository = pagingRepository;
        this.frameworkRepository = frameworkRepository;
        this.versionRepository = versionRepository;
        this.frameworkService = frameworkService;
    }

    public ProgrammingLanguage save(ProgrammingLanguage language) {
        return languageRepository.save(language);
    }

    public Iterable<ProgrammingLanguage> save(Iterable<ProgrammingLanguage> languages) {
        return languageRepository.saveAll(languages);
    }

    public ProgrammingLanguage findById(Long id) {
        return languageRepository.findById(id)
                                 .orElseThrow(() -> new ProgrammingLanguageNotFoundException(id));
    }

    public Page<ProgrammingLanguage> findPage(Pageable pageable) {
        return pagingRepository.findAll(pageable);
    }

    public Iterable<ProgrammingLanguage> findAll() {
        return languageRepository.findAll();
    }

    public int count() {
        return languageRepository.countAllBy();
    }

    public void update(Iterable<ProgrammingLanguage> languages) throws ProgrammingLanguageNotFoundException {
        throwNotFoundExIfNotPersisted(languages);
        save(languages);
    }

    public void deleteById(Long id) throws ProgrammingLanguageNotFoundException {
        throwNotFoundExIfNotPersisted(id);
        Iterable<Framework> frameworks = frameworkRepository.findByLanguage_Id(id);
        frameworks.forEach(f -> frameworkService.deleteById(f.getId()));
        languageRepository.deleteById(id);
    }

    public void deleteAll() {
        versionRepository.deleteAll();
        frameworkRepository.deleteAll();
        languageRepository.deleteAll();
    }

    public ProgrammingLanguage getOne(Long id) throws ProgrammingLanguageNotFoundException {
        ProgrammingLanguage one;
        try {
            one = languageRepository.getOne(id);
            one.setId(id);
        } catch (EntityNotFoundException ex) {
            throw new ProgrammingLanguageNotFoundException(id);
        }
        return one;
    }

    private void throwNotFoundExIfNotPersisted(Iterable<ProgrammingLanguage> languages)
            throws ProgrammingLanguageNotFoundException {
        languages.forEach(f -> this.throwNotFoundExIfNotPersisted(f.getId()));
    }

    private void throwNotFoundExIfNotPersisted(Long id) throws ProgrammingLanguageNotFoundException {
        getOne(id);
    }

}
