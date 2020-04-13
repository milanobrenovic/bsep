package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.dto.CertificateItemDTO;
import com.bsep.tim11.bseptim11.enumeration.CertificateRole;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

public interface KeyStoreService {

    void store(String keyStorePassword, String alias, PrivateKey privateKey, String keyPassword, Certificate[] certificates)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException;

    PrivateKey getPrivateKey(CertificateRole certificateRole, String keyStorePassword, String alias, String keyPassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException;

    PublicKey getPublicKey(CertificateRole certificateRole, String keyStorePassword, String alias)
        throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException;

    Certificate getCertificate(CertificateRole certificateRole, String keyStorePassword, String alias)
        throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException;

    List<CertificateItemDTO> getCertificates(String role, String keyStorePassword)
        throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException,
            OCSPException, OperatorCreationException;

    List<CertificateDTO> getCACertificates(Long subjectId, String rootKeyStoragePassword, String intermediateKeyStoragePassword)
        throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, NoSuchProviderException;

    Certificate[] getCertificateChain(CertificateRole certificateRole, String keyStorePassword, String alias)
        throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException;

    KeyStore getKeyStore(String keyStorePath, String keyStorePassword)
        throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException;

    String getKeyStorePath(CertificateRole certificateRole);

}
