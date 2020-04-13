package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.Framework;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Spring data repository interface used for accessing the data in database.
 *
 * @author Etnetera
 */
public interface FrameworkPagingRepository extends PagingAndSortingRepository<Framework, Long> {

    Page<Framework> findByLanguage(ProgrammingLanguage language, Pageable pageable);

    Page<Framework> findAll(Pageable pageable);

    int countAllBy();

}
