package com.etnetera.hr.service;

import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.data.repository.JavaScriptFrameworkPagingRepository;
import com.etnetera.hr.data.repository.JavaScriptFrameworkVersionRepository;
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
public class JavaScriptFrameworkService {

    private final JavaScriptFrameworkRepository frameworkRepository;
    private final JavaScriptFrameworkVersionRepository versionRepository;
    private final JavaScriptFrameworkPagingRepository frameworkPagingRepository;

    @Autowired
    public JavaScriptFrameworkService(JavaScriptFrameworkRepository frameworkRepository,
                                      JavaScriptFrameworkVersionRepository versionRepository,
                                      JavaScriptFrameworkPagingRepository frameworkPagingRepository) {
        this.frameworkRepository = frameworkRepository;
        this.versionRepository = versionRepository;
        this.frameworkPagingRepository = frameworkPagingRepository;
    }

    public JavaScriptFramework save(JavaScriptFramework framework) {
        return frameworkRepository.save(framework);
    }

    public Iterable<JavaScriptFramework> saveAll(Iterable<JavaScriptFramework> frameworks) {
        return frameworkRepository.saveAll(frameworks);
    }

    public Iterable<JavaScriptFramework> findAll() {
        return frameworkRepository.findAll();
    }

    public JavaScriptFramework findById(Long id) throws FrameworkNotFoundException {
        return frameworkRepository.findById(id)
                                  .orElseThrow(() -> new FrameworkNotFoundException(id));
    }

    public Page<JavaScriptFramework> findPage(Pageable pageable) {
        return frameworkPagingRepository.findAll(pageable);
    }

    public int count() {
        return frameworkPagingRepository.countAllBy();
    }

    public JavaScriptFramework getOne(Long id) throws FrameworkNotFoundException {
        JavaScriptFramework one;
        try {
            one = frameworkRepository.getOne(id);
            one.setId(id);
        } catch (EntityNotFoundException ex) {
            throw new FrameworkNotFoundException(id);
        }
        return one;
    }

    public void updateAll(Iterable<JavaScriptFramework> frameworks) throws FrameworkNotFoundException {
        throwNotFoundExIfNotPersisted(frameworks);
        saveAll(frameworks);
    }

    public void deleteById(Long id) throws FrameworkNotFoundException {
        throwNotFoundExIfNotPersisted(id);
        versionRepository.deleteAllByFramework_Id(id);
        frameworkRepository.deleteById(id);
    }

    public void deleteAll() {
        versionRepository.deleteAll();
        frameworkRepository.deleteAll();
    }

    private void throwNotFoundExIfNotPersisted(Iterable<JavaScriptFramework> frameworks)
            throws FrameworkNotFoundException {
        frameworks.forEach(f -> this.throwNotFoundExIfNotPersisted(f.getId()));
    }

    private void throwNotFoundExIfNotPersisted(Long id) throws FrameworkNotFoundException {
       getOne(id);
    }
}
