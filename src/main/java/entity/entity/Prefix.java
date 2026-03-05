package com.mednet.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "prefix")
public class Prefix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prefixName;
    private String gender;
    private String prefixOf;

    public Prefix() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPrefixOf() {
        return prefixOf;
    }

    public void setPrefixOf(String prefixOf) {
        this.prefixOf = prefixOf;
    }
}
