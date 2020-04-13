package com.bsep.tim11.bseptim11.service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public interface ValidationService {

    boolean validate(Certificate[] certificates)
        throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException;

}
