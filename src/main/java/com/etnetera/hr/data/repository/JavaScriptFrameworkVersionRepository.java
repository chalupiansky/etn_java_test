package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface JavaScriptFrameworkVersionRepository extends JpaRepository<JavaScriptFrameworkVersion, Long> {

    @Transactional
    void deleteAllByFramework(JavaScriptFramework framework);

    @Transactional
    void deleteAllByFramework_Id(Long id);

    Iterable<JavaScriptFrameworkVersion> findByFramework(JavaScriptFramework framework);

    int countAllByFramework(JavaScriptFramework framework);

    int countAllBy();
}
