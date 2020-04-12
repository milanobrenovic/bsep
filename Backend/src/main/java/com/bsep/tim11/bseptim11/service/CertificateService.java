package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.NewCertificateDTO;
import com.bsep.tim11.bseptim11.dto.ExistingCertificateDTO;
import com.bsep.tim11.bseptim11.dto.DownloadCertificateDTO;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface CertificateService {

    ExistingCertificateDTO create(NewCertificateDTO newCertificateDTO) throws Exception;
    ExistingCertificateDTO createSelfSigned(NewCertificateDTO newCertificateDTO) throws Exception;
    void download(DownloadCertificateDTO downloadCertificateDTO) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException;

}
