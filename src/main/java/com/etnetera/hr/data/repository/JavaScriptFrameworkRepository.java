package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.JavaScriptFramework;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring data repository interface used for accessing the data in database.
 */
public interface JavaScriptFrameworkRepository extends JpaRepository<JavaScriptFramework, Long> {
}
