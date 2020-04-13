package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.Framework;
import com.etnetera.hr.data.entity.FrameworkVersion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface FrameworkVersionPagingRepository extends PagingAndSortingRepository<FrameworkVersion, Long> {

    Page<FrameworkVersion> findByFramework(Framework framework, Pageable pageable);
}