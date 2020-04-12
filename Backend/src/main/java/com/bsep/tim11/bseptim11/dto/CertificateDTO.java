package com.bsep.tim11.bseptim11.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CertificateDTO {

    private String serialNumber;

    @NotNull(message = "Subject must not be empty.")
    private EntityDTO subject;

    @NotNull(message = "Issuer must not be empty.")
    private EntityDTO issuer;

    @NotNull(message = "Authority key identifier must not be empty.")
    private boolean authorityKeyIdentifier;

    @NotNull(message = "Subject key identifier must not be empty.")
    private boolean subjectKeyIdentifier;

    @NotNull(message = "Subject is certificate authority must not be empty.")
    private Boolean subjectIsCa;

    @NotEmpty(message = "Valid from cannot be empty.")
    private String validFrom;

    @NotEmpty(message = "Valid to cannot be empty.")
    private String validTo;

    @NotNull(message = "Key usage cannot be empty.")
    private KeyUsageDTO keyUsage;

    @NotNull(message = "Extended key usage cannot be empty.")
    private ExtendedKeyUsageDTO extendedKeyUsage;

    private String alias;

    public CertificateDTO() {

    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public EntityDTO getSubject() {
        return subject;
    }

    public void setSubject(EntityDTO subject) {
        this.subject = subject;
    }

    public EntityDTO getIssuer() {
        return issuer;
    }

    public void setIssuer(EntityDTO issuer) {
        this.issuer = issuer;
    }

    public boolean getAuthorityKeyIdentifier() {
        return authorityKeyIdentifier;
    }

    public void setAuthorityKeyIdentifier(boolean authorityKeyIdentifier) {
        this.authorityKeyIdentifier = authorityKeyIdentifier;
    }

    public boolean getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(boolean subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    public Boolean getSubjectIsCa() {
        return subjectIsCa;
    }

    public void setSubjectIsCa(Boolean subjectIsCa) {
        this.subjectIsCa = subjectIsCa;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public KeyUsageDTO getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(KeyUsageDTO keyUsage) {
        this.keyUsage = keyUsage;
    }

    public ExtendedKeyUsageDTO getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(ExtendedKeyUsageDTO extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
