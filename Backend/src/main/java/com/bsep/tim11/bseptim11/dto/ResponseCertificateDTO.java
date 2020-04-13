package com.bsep.tim11.bseptim11.dto;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

public class ResponseCertificateDTO {

    @NotEmpty(message = "Serial number cannot be empty.")
    private BigInteger serialNumber;

    @NotEmpty(message = "Valid from cannot be empty.")
    private String validFrom;

    @NotEmpty(message = "Valid to cannot be empty.")
    private String validTo;

    public ResponseCertificateDTO() {

    }

    public ResponseCertificateDTO(X509Certificate x509Certificate) {
        this.serialNumber = x509Certificate.getSerialNumber();
        this.validFrom = x509Certificate.getNotBefore().toString();
        this.validTo = x509Certificate.getNotAfter().toString();
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
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

}
