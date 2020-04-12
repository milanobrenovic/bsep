package com.bsep.tim11.bseptim11.dto;

import org.bouncycastle.asn1.x509.KeyUsage;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class KeyUsageDTO {

    @NotNull
    private Boolean certificateSigning;

    @NotNull
    private Boolean crlSign;

    @NotNull
    private Boolean dataEncipherment;

    @NotNull
    private Boolean decipherOnly;

    @NotNull
    private Boolean digitalSignature;

    @NotNull
    private Boolean encipherOnly;

    @NotNull
    private Boolean keyAgreement;

    @NotNull
    private Boolean keyEncipherment;

    @NotNull
    private Boolean nonRepudiation;

    public KeyUsageDTO() {

    }

    public KeyUsageDTO(boolean[] key) {
        this.digitalSignature = key[0];
        this.nonRepudiation = key[1];
        this.keyEncipherment = key[2];
        this.dataEncipherment = key[3];
        this.keyAgreement = key[4];
        this.certificateSigning = key[5];
        this.crlSign = key[6];
        this.encipherOnly = key[7];
        this.decipherOnly = key[8];
    }

    public Boolean getCertificateSigning() {
        return certificateSigning;
    }

    public int getCertificateSigningInt() {
        return certificateSigning ? KeyUsage.keyCertSign : 0;
    }

    public void setCertificateSigning(Boolean certificateSigning) {
        this.certificateSigning = certificateSigning;
    }

    public Boolean getCrlSign() {
        return crlSign;
    }

    public int getCrlSignInt() {
        return crlSign ? KeyUsage.cRLSign : 0;
    }

    public void setCrlSign(Boolean crlSign) {
        this.crlSign = crlSign;
    }

    public Boolean getDataEncipherment() {
        return dataEncipherment;
    }

    public int getDataEnciphermentInt() {
        return dataEncipherment ? KeyUsage.dataEncipherment : 0;
    }

    public void setDataEncipherment(Boolean dataEncipherment) {
        this.dataEncipherment = dataEncipherment;
    }

    public Boolean getDecipherOnly() {
        return decipherOnly;
    }

    public int getDecipherOnlyInt() {
        return decipherOnly ? KeyUsage.decipherOnly : 0;
    }

    public void setDecipherOnly(Boolean decipherOnly) {
        this.decipherOnly = decipherOnly;
    }

    public Boolean getDigitalSignature() {
        return digitalSignature;
    }

    public int getDigitalSignatureInt() {
        return digitalSignature ? KeyUsage.digitalSignature : 0;
    }

    public void setDigitalSignature(Boolean digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public Boolean getEncipherOnly() {
        return encipherOnly;
    }

    public int getEncipherOnlyInt() {
        return encipherOnly ? KeyUsage.encipherOnly : 0;
    }

    public void setEncipherOnly(Boolean encipherOnly) {
        this.encipherOnly = encipherOnly;
    }

    public Boolean getKeyAgreement() {
        return keyAgreement;
    }

    public int getKeyAgreementInt() {
        return keyAgreement ? KeyUsage.keyAgreement : 0;
    }

    public void setKeyAgreement(Boolean keyAgreement) {
        this.keyAgreement = keyAgreement;
    }

    public Boolean getKeyEncipherment() {
        return keyEncipherment;
    }

    public int getKeyEnciphermentInt() {
        return keyEncipherment ? KeyUsage.keyEncipherment : 0;
    }

    public void setKeyEncipherment(Boolean keyEncipherment) {
        this.keyEncipherment = keyEncipherment;
    }

    public Boolean getNonRepudiation() {
        return nonRepudiation;
    }

    public int getNonRepudiationInt() {
        return nonRepudiation ? KeyUsage.nonRepudiation : 0;
    }

    public void setNonRepudiation(Boolean nonRepudiation) {
        this.nonRepudiation = nonRepudiation;
    }

    public boolean isEnabled() {
        return certificateSigning || crlSign || dataEncipherment || decipherOnly || digitalSignature || encipherOnly ||
                keyAgreement || keyEncipherment || nonRepudiation;
    }

    public List<Integer> falseKeyUsageIdentifiers() {
        Boolean[] bools = {
            digitalSignature,
            nonRepudiation,
            keyEncipherment,
            dataEncipherment,
            keyAgreement,
            certificateSigning,
            crlSign,
            encipherOnly,
            decipherOnly
        };
        Integer[] keyUsages = {
            0,1,2,3,4,5,6,7,8
        };
        List<Integer> falseValues = new ArrayList<>();

        for (int i = 0; i < bools.length; i++) {
            if (!bools[i]) {
                falseValues.add(keyUsages[i]);
            }
        }

        return falseValues;
    }

}
