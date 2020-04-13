package com.etnetera.hr.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

/**
 * Simple data entity describing basic properties of every framework.
 *
 * @author Etnetera
 */
@Entity
public class Framework {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column(unique = true, length = 30, nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    private ProgrammingLanguage language;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Framework)) return false;
        Framework framework = (Framework) o;
        return Objects.equals(getId(), framework.getId()) &&
                Objects.equals(getName(), framework.getName()) &&
                Objects.equals(getLanguage(), framework.getLanguage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getLanguage());
    }
}
