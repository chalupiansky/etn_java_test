package com.etnetera.hr.service;

import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;
import com.etnetera.hr.data.repository.JavaScriptFrameworkVersionRepository;
import com.etnetera.hr.data.repository.JavaScriptFrameworkVersionPagingRepository;
import com.etnetera.hr.rest.exception.FrameworkVersionNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

/**
 * Service used for accessing the data in database.
 */
@Service
public class JavaScriptFrameworkVersionService {

    private final JavaScriptFrameworkVersionRepository versionRepository;
    private final JavaScriptFrameworkVersionPagingRepository versionPagingRepository;

    @Autowired
    public JavaScriptFrameworkVersionService(JavaScriptFrameworkVersionRepository versionRepository,
                                             JavaScriptFrameworkVersionPagingRepository versionPagingRepository) {
        this.versionRepository = versionRepository;
        this.versionPagingRepository = versionPagingRepository;
    }

    public JavaScriptFrameworkVersion save(JavaScriptFrameworkVersion version) {
        return versionRepository.save(version);
    }

    public Iterable<JavaScriptFrameworkVersion> saveAll(Iterable<JavaScriptFrameworkVersion> versions) {
        return versionRepository.saveAll(versions);
    }

    public Iterable<JavaScriptFrameworkVersion> findAll() {
        return versionRepository.findAll();
    }

    public JavaScriptFrameworkVersion findById(Long id) throws FrameworkVersionNotFoundException {
        return versionRepository.findById(id)
                                .orElseThrow(() -> new FrameworkVersionNotFoundException(id));
    }

    public Iterable<JavaScriptFrameworkVersion> findByFramework(JavaScriptFramework framework) {
        return versionRepository.findByFramework(framework);
    }

    public Page<JavaScriptFrameworkVersion> findByFramework(JavaScriptFramework framework, Pageable pageable) {
        return versionPagingRepository.findByFramework(framework, pageable);
    }

    public int count() {
        return versionRepository.countAllBy();
    }

    public int countByFramework(JavaScriptFramework framework) {
        return versionRepository.countAllByFramework(framework);
    }

    public void updateAll(Iterable<JavaScriptFrameworkVersion> versions) throws FrameworkVersionNotFoundException {
        throwNotFoundExIfNotPersisted(versions);
        saveAll(versions);
    }

    public void deleteById(Long id) throws FrameworkVersionNotFoundException {
        throwNotFoundExIfNotPersisted(id);
        versionRepository.deleteById(id);
    }

    public void deleteAllByFramework(JavaScriptFramework framework) {
        versionRepository.deleteAllByFramework(framework);
    }

    public void deleteAll() {
        versionRepository.deleteAll();
    }

    private void throwNotFoundExIfNotPersisted(Iterable<JavaScriptFrameworkVersion> versions)
            throws FrameworkVersionNotFoundException {
        versions.forEach(v -> this.throwNotFoundExIfNotPersisted(v.getId()));
    }

    private void throwNotFoundExIfNotPersisted(Long id) throws FrameworkVersionNotFoundException {
        try {
            JavaScriptFrameworkVersion one = versionRepository.getOne(id);
            one.setId(id);
        } catch (EntityNotFoundException ex) {
            throw new FrameworkVersionNotFoundException(id);
        }
    }
}
