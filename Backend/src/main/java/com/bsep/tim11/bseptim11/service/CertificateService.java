package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.NewCertificateDTO;
import com.bsep.tim11.bseptim11.dto.ExistingCertificateDTO;
import com.bsep.tim11.bseptim11.dto.DownloadCertificateDTO;
import com.bsep.tim11.bseptim11.dto.ResponseCertificateDTO;
import org.bouncycastle.operator.OperatorCreationException;
import org.ietf.jgss.GSSException;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.ParseException;

public interface CertificateService {

    ResponseCertificateDTO create(NewCertificateDTO newCertificateDTO) throws
            ParseException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, GSSException,
            OperatorCreationException;

    ResponseCertificateDTO createSelfSigned(NewCertificateDTO newCertificateDTO) throws
            ParseException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
            IOException, CertificateException, OperatorCreationException, GSSException, KeyStoreException;

    void download(DownloadCertificateDTO downloadCertificateDTO) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException;

}
