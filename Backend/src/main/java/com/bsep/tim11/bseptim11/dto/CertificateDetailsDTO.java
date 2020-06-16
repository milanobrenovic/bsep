package com.bsep.tim11.bseptim11.dto;

import java.math.BigInteger;
import java.util.Date;

public class CertificateDetailsDTO {

    private String subjectName;
    private String issuerName;
    private BigInteger serialNumber;
    private Date validFrom;
    private Date validTo;
    private String alias;

    public CertificateDetailsDTO() {

    }

    public CertificateDetailsDTO(String subjectName, String issuerName, BigInteger serialNumber, Date validFrom, Date validTo, String alias) {
        this.subjectName = subjectName;
        this.issuerName = issuerName;
        this.serialNumber = serialNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.alias = alias;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "CertificateDetailsDTO{" +
                "subjectName='" + subjectName + '\'' +
                ", issuerName='" + issuerName + '\'' +
                ", serialNumber=" + serialNumber +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", alias='" + alias + '\'' +
                '}';
    }
}
