package com.bsep.tim11.bseptim11.dto;

import org.bouncycastle.asn1.x509.KeyPurposeId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ExtendedKeyUsageDTO {

    @NotNull(message = "Server auth cannot be empty.")
    private Boolean serverAuth;

    @NotNull(message = "Client auth cannot be empty.")
    private Boolean clientAuth;

    @NotNull(message = "Code signing cannot be empty.")
    private Boolean codeSigning;

    @NotNull(message = "Email protection cannot be empty.")
    private Boolean emailProtection;

    @NotNull(message = "Time stamping cannot be empty.")
    private Boolean timeStamping;

    @NotNull(message = "OCSP signing cannot be empty.")
    private Boolean ocspSigning;

    public ExtendedKeyUsageDTO() {

    }

    public ExtendedKeyUsageDTO(List<String> extendedKeyUsage) {
        String idkp = "1.3.6.1.5.5.7.3";
        this.serverAuth = false;
        this.clientAuth = false;
        this.codeSigning = false;
        this.emailProtection = false;
        this.ocspSigning = false;
        this.timeStamping = false;

        if (extendedKeyUsage.contains(idkp + ".1")) {
            this.serverAuth = true;
        }
        if (extendedKeyUsage.contains(idkp + ".2")) {
            this.clientAuth = true;
        }
        if (extendedKeyUsage.contains(idkp + ".3")) {
            this.codeSigning = true;
        }
        if (extendedKeyUsage.contains(idkp + ".8")) {
            this.timeStamping = true;
        }
        if (extendedKeyUsage.contains(idkp + ".4")) {
            this.emailProtection = true;
        }
        if (extendedKeyUsage.contains(idkp + ".9")) {
            this.ocspSigning = true;
        }
    }

    public Boolean getServerAuth() {
        return serverAuth;
    }

    public void setServerAuth(Boolean serverAuth) {
        this.serverAuth = serverAuth;
    }

    public Boolean getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(Boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    public Boolean getCodeSigning() {
        return codeSigning;
    }

    public void setCodeSigning(Boolean codeSigning) {
        this.codeSigning = codeSigning;
    }

    public Boolean getEmailProtection() {
        return emailProtection;
    }

    public void setEmailProtection(Boolean emailProtection) {
        this.emailProtection = emailProtection;
    }

    public Boolean getTimeStamping() {
        return timeStamping;
    }

    public void setTimeStamping(Boolean timeStamping) {
        this.timeStamping = timeStamping;
    }

    public Boolean getOcspSigning() {
        return ocspSigning;
    }

    public void setOcspSigning(Boolean ocspSigning) {
        this.ocspSigning = ocspSigning;
    }

    public boolean isEnabled() {
        return serverAuth || clientAuth || codeSigning || emailProtection || timeStamping || ocspSigning;
    }

    public KeyPurposeId[] keyPurposeIds() {
        Boolean[] bools = {
            serverAuth,
            clientAuth,
            codeSigning,
            emailProtection,
            timeStamping,
            ocspSigning
        };
        /*

    public static final KeyPurposeId id_kp_serverAuth;
    public static final KeyPurposeId id_kp_clientAuth;
    public static final KeyPurposeId id_kp_codeSigning;
    public static final KeyPurposeId id_kp_emailProtection;

    public static final KeyPurposeId id_kp_ipsecEndSystem;
    public static final KeyPurposeId id_kp_ipsecTunnel;
    public static final KeyPurposeId id_kp_ipsecUser;

    public static final KeyPurposeId id_kp_timeStamping;
    public static final KeyPurposeId id_kp_OCSPSigning;

    public static final KeyPurposeId id_kp_dvcs;
    public static final KeyPurposeId id_kp_sbgpCertAAServerAuth;
    public static final KeyPurposeId id_kp_scvp_responder;
    public static final KeyPurposeId id_kp_eapOverPPP;
    public static final KeyPurposeId id_kp_eapOverLAN;
    public static final KeyPurposeId id_kp_scvpServer;
    public static final KeyPurposeId id_kp_scvpClient;
    public static final KeyPurposeId id_kp_ipsecIKE;
    public static final KeyPurposeId id_kp_capwapAC;
    public static final KeyPurposeId id_kp_capwapWTP;
    public static final KeyPurposeId id_kp_smartcardlogon;
    public static final KeyPurposeId id_kp_macAddress;
    public static final KeyPurposeId id_kp_msSGC;
    public static final KeyPurposeId id_kp_nsSGC;
         */
        KeyPurposeId[] keyPurposeIds = {
            KeyPurposeId.id_kp_serverAuth,
            KeyPurposeId.id_kp_clientAuth,
            KeyPurposeId.id_kp_codeSigning,
            KeyPurposeId.id_kp_emailProtection,
            KeyPurposeId.id_kp_timeStamping,
            KeyPurposeId.id_kp_OCSPSigning
        };
        List<KeyPurposeId> setPurposes = new ArrayList<>();

        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) {
                setPurposes.add(keyPurposeIds[i]);
            }
        }

        KeyPurposeId[] newKeyPurposeIds = new KeyPurposeId[setPurposes.size()];
        for (int i = 0; i < setPurposes.size(); i++) {
            newKeyPurposeIds[i] = setPurposes.get(i);
        }

        return newKeyPurposeIds;
    }

    public List<KeyPurposeId> falseExtendedKeyUsageIdentifiers() {
        Boolean[] bools = {
                serverAuth,
                clientAuth,
                codeSigning,
                emailProtection,
                timeStamping,
                ocspSigning
        };
        KeyPurposeId[] keyPurposeIds = {
                KeyPurposeId.id_kp_serverAuth,
                KeyPurposeId.id_kp_clientAuth,
                KeyPurposeId.id_kp_codeSigning,
                KeyPurposeId.id_kp_emailProtection,
                KeyPurposeId.id_kp_timeStamping,
                KeyPurposeId.id_kp_OCSPSigning
        };
        List<KeyPurposeId> setPurposes = new ArrayList<>();

        for (int i = 0; i < bools.length; i++) {
            if (!bools[i]) {
                setPurposes.add(keyPurposeIds[i]);
            }
        }

        return setPurposes;
    }

}
