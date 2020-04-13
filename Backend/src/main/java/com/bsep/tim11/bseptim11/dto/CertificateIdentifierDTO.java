package com.bsep.tim11.bseptim11.dto;

import org.bouncycastle.cert.ocsp.CertificateID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CertificateIdentifierDTO {

    @NotEmpty(message = "Serial number cannot be empty.")
    private String serialNumber;

    @NotEmpty(message = "Issuer name hash cannot be empty.")
    private String issuerNameHash;

    @NotEmpty(message = "Issuer key hash cannot be empty.")
    private String issuerKeyHash;

    @NotEmpty(message = "Hash algorithm cannot be empty.")
    private String hashAlgorithm;

    public CertificateIdentifierDTO() {

    }

    public CertificateIdentifierDTO(CertificateID certificateID) {
        this.serialNumber = certificateID.getSerialNumber().toString();
        this.issuerNameHash = new String(certificateID.getIssuerNameHash());
        this.issuerKeyHash = new String(certificateID.getIssuerKeyHash());
        this.hashAlgorithm = "sha1";
    }

    public CertificateIdentifierDTO(
            @NotNull(message = "Serial number cannot be null.") String serialNumber,
            @NotEmpty(message = "Issuer name hash cannot be empty.") String issuerNameHash,
            @NotEmpty(message = "Issuer key hash cannot be empty.") String issuerKeyHash,
            @NotEmpty(message = "Hash algorithm cannot be empty.") String hashAlgorithm) {

        this.serialNumber = serialNumber;
        this.issuerNameHash = issuerNameHash;
        this.issuerKeyHash = issuerKeyHash;
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuerNameHash() {
        return issuerNameHash;
    }

    public void setIssuerNameHash(String issuerNameHash) {
        this.issuerNameHash = issuerNameHash;
    }

    public String getIssuerKeyHash() {
        return issuerKeyHash;
    }

    public void setIssuerKeyHash(String issuerKeyHash) {
        this.issuerKeyHash = issuerKeyHash;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

}
