package com.etnetera.hr.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

/**
 * Simple data entity describing basic properties of every JavaScript framework name.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "framework_fk"})})
public class JavaScriptFrameworkVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(nullable = false, length = 15)
    private String name;

    @Temporal(value = TemporalType.DATE)
    private Date deprecationDate;

    @Min(value = 0)
    @Max(value = 100)
    @Column
    private int hypeLevel;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_fk", updatable = false)
    private JavaScriptFramework framework;

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

    public Date getDeprecationDate() {
        return deprecationDate;
    }

    public void setDeprecationDate(Date deprecationDate) {
        this.deprecationDate = deprecationDate;
    }

    public int getHypeLevel() {
        return hypeLevel;
    }

    public void setHypeLevel(int hypeLevel) {
        this.hypeLevel = hypeLevel;
    }

    public JavaScriptFramework getFramework() {
        return framework;
    }

    public void setFramework(JavaScriptFramework framework) {
        this.framework = framework;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaScriptFrameworkVersion)) return false;
        JavaScriptFrameworkVersion that = (JavaScriptFrameworkVersion) o;
        return getHypeLevel() == that.getHypeLevel() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDeprecationDate(), that.getDeprecationDate()) &&
                Objects.equals(getFramework(), that.getFramework());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDeprecationDate(), getHypeLevel(), getFramework());
    }
}
