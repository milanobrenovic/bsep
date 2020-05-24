package com.bsep.tim11.bseptim11.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Subject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "commonname")
    private String commonName;
	
	@Column(name = "surname")
    private String surname;
	
	@Column(name = "givenname")
    private String givenName;
	
	@Column(name = "organization")
    private String organization;
	
	@Column(name = "organizationunit")
    private String organizationUnit;
	
	@Column(name = "country")
    private String country;
	
	@Column(name = "email")
    private String email;
    
	@Column(name = "isca")
	private Boolean isCA;
	
	@Column(name = "hascertificate")
	private Boolean hasCertificate;
	
    public Subject() {
    	
    }

	public Subject(String commonName, String surname, String givenName,
			String organization, String organizationUnit, String country, String email, Boolean isCA, Boolean hasCertificate) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
