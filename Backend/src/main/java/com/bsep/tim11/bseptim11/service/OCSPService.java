package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.ResponseCertificateDTO;
import com.bsep.tim11.bseptim11.dto.RevokeCertificateDTO;
import com.bsep.tim11.bseptim11.enumeration.CertificateStatus;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public interface OCSPService {

    CertificateStatus checkStatus(String serialNumber);

    ResponseCertificateDTO revoke(RevokeCertificateDTO revokeCertificateDTO)
        throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException;

    boolean isRevoked(Certificate certificate);

}
