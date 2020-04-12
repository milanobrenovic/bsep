package com.bsep.tim11.bseptim11.dto;

import com.bsep.tim11.bseptim11.model.Entity;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class EntityDTO {

    private Long id;

    @NotEmpty(message = "Type cannot be empty.")
    private String type;

    @NotEmpty(message = "Common name cannot be empty.")
    private String commonName;

    @Email
    private String email;

    private String organizationUnit;

    @NotEmpty(message = "Organization cannot be empty.")
    private String organization;

    @NotEmpty(message = "Country code cannot be empty.")
    @Size(message = "Country code must be exactly 2 characters.", min = 2, max = 2)
    private String countryCode;

    private String surname;

    private String givename;

    public EntityDTO() {

    }

    public EntityDTO(
        Long id,
        @NotEmpty(message = "Type cannot be empty.") String type,
        @NotEmpty(message = "Common name cannot be empty.") String commonName,
        @Email String email,
        String organizationUnit,
        @NotEmpty(message = "Organization cannot be empty.") String organization,
        @NotEmpty(message = "Country code cannot be empty.")
        @Size(message = "Country code must be exactly 2 characters.", min = 2, max = 2) String countryCode,
        String surname,
        String givename) {
        this.id = id;
        this.type = type;
        this.commonName = commonName;
        this.email = email;
        this.organizationUnit = organizationUnit;
        this.organization = organization;
        this.countryCode = countryCode;
        this.surname = surname;
        this.givename = givename;
    }

    public EntityDTO(Entity entity) {
        this(entity.getId(), entity.getType().toString(), entity.getCommonName(), entity.getEmail(), entity.getOrganizationUnit(),
                entity.getOrganization(), entity.getCountryCode(), entity.getSurname(), entity.getGivename());
    }

    public EntityDTO(X500Name x500Name, Long id) {
        this.commonName = x500Name.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString();
        this.organization = x500Name.getRDNs(BCStyle.O)[0].getFirst().getValue().toString();
        this.countryCode = x500Name.getRDNs(BCStyle.C)[0].getFirst().getValue().toString();
        RDN[] email = x500Name.getRDNs(BCStyle.EmailAddress);

        if (email != null && email.length != 0) {
            this.email = email[0].getFirst().getValue().toString();
            this.surname = x500Name.getRDNs(BCStyle.SURNAME)[0].getFirst().getValue().toString();
            this.givename = x500Name.getRDNs(BCStyle.GIVENNAME)[0].getFirst().getValue().toString();
            this.type = "USER";
        } else {
            this.organizationUnit = x500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString();
            this.type = "COMPANY";
        }

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

}
