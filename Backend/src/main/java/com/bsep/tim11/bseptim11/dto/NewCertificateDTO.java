package com.bsep.tim11.bseptim11.dto;

import javax.validation.constraints.NotNull;

public class NewCertificateDTO {

    @NotNull(message = "Certificate data cannot be empty.")
    private CertificateDTO certificateData;

    @NotNull(message = "Issuer certificate cannot be empty.")
    private CertificateDTO issuerCertificate;

    @NotNull(message = "Alias cannot be empty.")
    private String alias;

    @NotNull(message = "Keystore password cannot be empty.")
    private String keyStorePassword;

    @NotNull(message = "Private key password cannot be empty.")
    private String privateKeyPassword;

    private String issuerKeyStorePassword;

    private String issuerPrivateKeyPassword;

    public NewCertificateDTO() {

    }

    public CertificateDTO getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(CertificateDTO certificateData) {
        this.certificateData = certificateData;
    }

    public CertificateDTO getIssuerCertificate() {
        return issuerCertificate;
    }

    public void setIssuerCertificate(CertificateDTO issuerCertificate) {
        this.issuerCertificate = issuerCertificate;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public String getIssuerKeyStorePassword() {
        return issuerKeyStorePassword;
    }

    public void setIssuerKeyStorePassword(String issuerKeyStorePassword) {
        this.issuerKeyStorePassword = issuerKeyStorePassword;
    }

    public String getIssuerPrivateKeyPassword() {
        return issuerPrivateKeyPassword;
    }

    public void setIssuerPrivateKeyPassword(String issuerPrivateKeyPassword) {
        this.issuerPrivateKeyPassword = issuerPrivateKeyPassword;
    }

    @Override
    public String toString() {
        return "CreateCertificateDTO{" +
                "certificateData=" + certificateData +
                ", issuerCertificate=" + issuerCertificate +
                ", alias='" + alias + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", privateKeyPassword='" + privateKeyPassword + '\'' +
                ", issuerKeyStorePassword='" + issuerKeyStorePassword + '\'' +
                ", issuerPrivateKeyPassword='" + issuerPrivateKeyPassword + '\'' +
                '}';
    }
}
