package com.bsep.tim11.bseptim11.dto;

import com.bsep.tim11.bseptim11.model.Subject;

public class SubjectDTO {

    private String commonName;
    private String surname;	
    private String givenName;	
    private String organization;	
    private String organizationUnit;	
    private String country;	
    private String email;
    private Boolean isCA;
    private Boolean hasCertificate;
    
    public SubjectDTO() {
    	
    }
    
	public SubjectDTO(String commonName, String surname, String givenName, String organization, String organizationUnit,
			String country, String email, Boolean isCA, Boolean hasCertificate) {
		super();
		this.commonName = commonName;
		this.surname = surname;
		this.givenName = givenName;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.country = country;
		this.email = email;
		this.isCA = isCA;
		this.hasCertificate = hasCertificate;
	}
	
	public SubjectDTO(Subject s) {
		super();
		this.commonName = s.getCommonName();
		this.surname = s.getSurname();
		this.givenName = s.getGivenName();
		this.organization = s.getOrganization();
		this.organizationUnit = s.getOrganizationUnit();
		this.country = s.getCountry();
		this.email = s.getEmail();
		this.isCA = s.getIsCA();
		this.hasCertificate = s.getHasCertificate();
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsCA() {
		return isCA;
	}

	public void setIsCA(Boolean isCA) {
		this.isCA = isCA;
	}

	public Boolean getHasCertificate() {
		return hasCertificate;
	}

	public void setHasCertificate(Boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}
    
    
}
