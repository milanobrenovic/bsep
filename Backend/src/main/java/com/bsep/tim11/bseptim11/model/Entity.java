package com.bsep.tim11.bseptim11.model;

import com.bsep.tim11.bseptim11.enumeration.EntityType;

import javax.persistence.*;
import javax.validation.constraints.Email;

@javax.persistence.Entity
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EntityType type;

    @Column(columnDefinition = "VARCHAR(64)", nullable = false, unique = true)
    private String commonName;

    @Column
    private String email;

    @Column(columnDefinition = "VARCHAR(64)")
    private String organizationUnit;

    @Column(columnDefinition = "VARCHAR(64)", nullable = false)
    private String organization;

    @Column(columnDefinition = "VARCHAR(2)", nullable = false)
    private String countryCode;

    @Column
    private String surname;

    @Column
    private String givename;

    @Column
    private Integer numberOfRootCertificates;

    public Entity() {
        this.numberOfRootCertificates = 0;
    }

    public Entity(
        EntityType type,
        String commonName,
        String email,
        String organizationUnit,
        String organization,
        String countryCode,
        String surname,
        String givename
    ) {
        this.type = type;
        this.commonName = commonName;
        this.countryCode = countryCode;
        this.organization = organization;

        if (type.toString().equals("USER")) {
            this.email = email;
            this.surname = surname;
            this.givename = givename;
            this.organizationUnit = "";
        } else {
            this.email = "";
            this.surname = "";
            this.givename = "";
            this.organizationUnit = organizationUnit;
        }

        this.numberOfRootCertificates = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivename() {
        return givename;
    }

    public void setGivename(String givename) {
        this.givename = givename;
    }

    public Integer getNumberOfRootCertificates() {
        return numberOfRootCertificates;
    }

    public void setNumberOfRootCertificates(Integer numberOfRootCertificates) {
        this.numberOfRootCertificates = numberOfRootCertificates;
    }

}
