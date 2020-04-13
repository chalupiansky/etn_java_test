package com.etnetera.hr.service;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;
import com.etnetera.hr.data.repository.FrameworkVersionRepository;
import com.etnetera.hr.data.repository.FrameworkVersionPagingRepository;
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
public class FrameworkVersionService {

    private final FrameworkVersionRepository versionRepository;
    private final FrameworkVersionPagingRepository versionPagingRepository;

    @Autowired
    public FrameworkVersionService(FrameworkVersionRepository versionRepository,
                                   FrameworkVersionPagingRepository versionPagingRepository) {
        this.versionRepository = versionRepository;
        this.versionPagingRepository = versionPagingRepository;
    }

    public FrameworkVersion save(FrameworkVersion version) {
        return versionRepository.save(version);
    }

    public Iterable<FrameworkVersion> saveAll(Iterable<FrameworkVersion> versions) {
        return versionRepository.saveAll(versions);
    }

    public Iterable<FrameworkVersion> findAll() {
        return versionRepository.findAll();
    }

    public FrameworkVersion findById(Long id) throws FrameworkVersionNotFoundException {
        return versionRepository.findById(id)
                                .orElseThrow(() -> new FrameworkVersionNotFoundException(id));
    }

    public Iterable<FrameworkVersion> findByFramework(Framework framework) {
        return versionRepository.findByFramework(framework);
    }

    public Page<FrameworkVersion> findByFramework(Framework framework, Pageable pageable) {
        return versionPagingRepository.findByFramework(framework, pageable);
    }

    public int count() {
        return versionRepository.countAllBy();
    }

    public int countByFramework(Framework framework) {
        return versionRepository.countAllByFramework(framework);
    }

    public void updateAll(Iterable<FrameworkVersion> versions) throws FrameworkVersionNotFoundException {
        throwNotFoundExIfNotPersisted(versions);
        saveAll(versions);
    }

    public void deleteById(Long id) throws FrameworkVersionNotFoundException {
        throwNotFoundExIfNotPersisted(id);
        versionRepository.deleteById(id);
    }

    public void deleteAllByFramework(Framework framework) {
        versionRepository.deleteAllByFramework(framework);
    }

    public void deleteAll() {
        versionRepository.deleteAll();
    }

    private void throwNotFoundExIfNotPersisted(Iterable<FrameworkVersion> versions)
            throws FrameworkVersionNotFoundException {
        versions.forEach(v -> this.throwNotFoundExIfNotPersisted(v.getId()));
    }

    private void throwNotFoundExIfNotPersisted(Long id) throws FrameworkVersionNotFoundException {
        try {
            FrameworkVersion one = versionRepository.getOne(id);
            one.setId(id);
        } catch (EntityNotFoundException ex) {
            throw new FrameworkVersionNotFoundException(id);
        }
    }
}
