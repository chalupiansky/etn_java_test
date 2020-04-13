package com.etnetera.hr.data.repository;

import com.etnetera.hr.data.entity.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgrammingLanguageRepository extends JpaRepository<ProgrammingLanguage, Long> {

    int countAllBy();
}
