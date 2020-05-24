package com.bsep.tim11.bseptim11.model;

import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;

public class SubjectData {

    private PublicKey publicKey;
    private X500Name x500name;
    private String serialNumber;

    public SubjectData() {

    }

    public SubjectData(PublicKey publicKey, X500Name x500name, String serialNumber) {
        this.publicKey = publicKey;
        this.x500name = x500name;
        this.serialNumber = serialNumber;

    }

    public X500Name getX500name() {
        return x500name;
    }

    public void setX500name(X500Name x500name) {
        this.x500name = x500name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    

}
