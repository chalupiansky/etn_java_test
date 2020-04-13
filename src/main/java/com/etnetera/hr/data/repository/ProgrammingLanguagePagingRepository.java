package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProgrammingLanguagePagingRepository extends PagingAndSortingRepository<ProgrammingLanguage, Long> {
}
