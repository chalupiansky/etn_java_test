package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface FrameworkVersionRepository extends JpaRepository<FrameworkVersion, Long> {

    @Transactional
    void deleteAllByFramework(Framework framework);

    @Transactional
    void deleteAllByFramework_Id(Long id);

    Iterable<FrameworkVersion> findByFramework(Framework framework);

    int countAllByFramework(Framework framework);

    int countAllBy();
}
