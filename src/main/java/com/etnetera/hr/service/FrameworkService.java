package com.etnetera.hr.service;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.ProgrammingLanguage;
import com.etnetera.hr.data.repository.FrameworkRepository;
import com.etnetera.hr.data.repository.FrameworkPagingRepository;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.rest.exception.FrameworkNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

/**
 * Service used for accessing the data in database.
 */
@Service
public class FrameworkService {

    private final FrameworkRepository frameworkRepository;
    private final FrameworkVersionRepository versionRepository;
    private final FrameworkPagingRepository frameworkPagingRepository;

    @Autowired
    public FrameworkService(FrameworkRepository frameworkRepository,
                            FrameworkVersionRepository versionRepository,
                            FrameworkPagingRepository frameworkPagingRepository) {
        this.frameworkRepository = frameworkRepository;
        this.versionRepository = versionRepository;
        this.frameworkPagingRepository = frameworkPagingRepository;
    }

    public Framework save(Framework framework) {
        return frameworkRepository.save(framework);
    }

    public Iterable<Framework> saveAll(Iterable<Framework> frameworks) {
        return frameworkRepository.saveAll(frameworks);
    }

    public Iterable<Framework> findAll() {
        return frameworkRepository.findAll();
    }

    public Framework findById(Long id) throws FrameworkNotFoundException {
        return frameworkRepository.findById(id)
                                  .orElseThrow(() -> new FrameworkNotFoundException(id));
    }

    public Iterable<Framework> findByLanguage(ProgrammingLanguage language) {
        return frameworkRepository.findByLanguage(language);
    }

    public Page<Framework> findByLanguage(ProgrammingLanguage language, Pageable pageable) {
        return frameworkPagingRepository.findByLanguage(language, pageable);
    }


    public int count() {
        return frameworkPagingRepository.countAllBy();
    }

    public int countByLanguage(ProgrammingLanguage language) {
        return frameworkRepository.countAllByLanguage(language);
    }

    public Framework getOne(Long id) throws FrameworkNotFoundException {
        Framework one;
        try {
            one = frameworkRepository.getOne(id);
            one.setId(id);
        } catch (EntityNotFoundException ex) {
            throw new FrameworkNotFoundException(id);
        }
        return one;
    }

    public void updateAll(Iterable<Framework> frameworks) throws FrameworkNotFoundException {
        throwNotFoundExIfNotPersisted(frameworks);
        saveAll(frameworks);
    }

    public void deleteById(Long id) throws FrameworkNotFoundException {
        throwNotFoundExIfNotPersisted(id);
        versionRepository.deleteAllByFramework_Id(id);
        frameworkRepository.deleteById(id);
    }

    public void deleteAllByLanguage(ProgrammingLanguage language) {
        Iterable<Framework> frameworks = findByLanguage(language);
        frameworks.forEach(f -> deleteById(f.getId()));
    }

    public void deleteAll() {
        versionRepository.deleteAll();
        frameworkRepository.deleteAll();
    }

    private void throwNotFoundExIfNotPersisted(Iterable<Framework> frameworks)
            throws FrameworkNotFoundException {
        frameworks.forEach(f -> this.throwNotFoundExIfNotPersisted(f.getId()));
    }

    private void throwNotFoundExIfNotPersisted(Long id) throws FrameworkNotFoundException {
       getOne(id);
    }
}
