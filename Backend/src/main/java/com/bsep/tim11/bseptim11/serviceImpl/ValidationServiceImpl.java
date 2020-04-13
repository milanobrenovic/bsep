package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.service.OCSPService;
import com.bsep.tim11.bseptim11.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private OCSPService ocspService;

    @Override
    public boolean validate(Certificate[] certificates) throws
            CertificateException, NoSuchProviderException, NoSuchAlgorithmException {

        if (!validateRoot(certificates[certificates.length - 1])) {
            return false;
        }

        for (int i = certificates.length - 1; i > 0; i--) {
            if (!validateCert(certificates[i - 1], certificates[i].getPublicKey())) {
                return false;
            }
        }

        return true;
    }

    private boolean validateRoot(Certificate certificate) throws
            CertificateException, NoSuchProviderException, NoSuchAlgorithmException {

        try {
            PublicKey publicKey = certificate.getPublicKey();
            certificate.verify(publicKey);

            if (!validateCert(certificate, publicKey)) {
                return false;
            }

            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        }
    }

    private boolean verifySignature(Certificate certificate, PublicKey issuerPublicKey) throws
            CertificateException, NoSuchProviderException, NoSuchAlgorithmException {

        try {
            certificate.verify(issuerPublicKey);
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        }
    }

    private boolean validateCert(Certificate certificate, PublicKey issuerPublicKey) throws
            NoSuchAlgorithmException, CertificateException, NoSuchProviderException {

        try {
            ((X509Certificate) certificate).checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            return false;
        }

        if (!verifySignature(certificate, issuerPublicKey)) {
            return false;
        }

        return !ocspService.isRevoked(certificate);
    }

}
