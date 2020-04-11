package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.JavaScriptFramework;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Spring data repository interface used for accessing the data in database.
 *
 * @author Etnetera
 */
public interface JavaScriptFrameworkPagingRepository extends PagingAndSortingRepository<JavaScriptFramework, Long> {

    Page<JavaScriptFramework> findAll(Pageable pageable);

    int countAllBy();

}
