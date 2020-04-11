package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.JavaScriptFramework;
import com.etnetera.hr.data.entity.JavaScriptFrameworkVersion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface JavaScriptFrameworkVersionPagingRepository extends PagingAndSortingRepository<JavaScriptFrameworkVersion, Long> {

    Page<JavaScriptFrameworkVersion> findByFramework(JavaScriptFramework framework, Pageable pageable);
}