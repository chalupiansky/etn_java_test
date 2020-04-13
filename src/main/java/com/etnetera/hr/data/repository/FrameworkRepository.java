package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.Framework;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface FrameworkRepository extends JpaRepository<Framework, Long> {

    Iterable<Framework> findByLanguage_Id(Long id);

    Iterable<Framework> findByLanguage(ProgrammingLanguage language);

    int countAllByLanguage(ProgrammingLanguage language);
}
