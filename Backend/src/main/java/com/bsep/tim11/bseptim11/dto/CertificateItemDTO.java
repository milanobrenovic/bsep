package com.bsep.tim11.bseptim11.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CertificateItemDTO {

    @NotEmpty(message = "Serial number cannot be empty.")
    private String serialNumber;

    @NotNull(message = "Subject entity cannot be null.")
    private EntityDTO subject;

    @NotNull(message = "Issuer entity cannot be null.")
    private EntityDTO issuer;

    @NotNull(message = "Subject is CA cannot be null.")
    private Boolean subjectIsCa;

    @NotEmpty(message = "Valid from cannot be empty.")
    private String validFrom;

    @NotEmpty(message = "Valid to cannot be empty.")
    private String validTo;

    @NotNull(message = "Key usage cannot be null.")
    private KeyUsageDTO keyUsage;

    @NotNull(message = "Extended key usage cannot be null.")
    private ExtendedKeyUsageDTO extendedKeyUsage;

    @NotEmpty(message = "Alias cannot be empty.")
    private String alias;

    @NotNull(message = "Certificate identifier cannot be null.")
    private CertificateIdentifierDTO certificateIdentifier;

    public CertificateItemDTO() {

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

    public CertificateIdentifierDTO getCertificateIdentifier() {
        return certificateIdentifier;
    }

    public void setCertificateIdentifier(CertificateIdentifierDTO certificateIdentifier) {
        this.certificateIdentifier = certificateIdentifier;
    }

}
