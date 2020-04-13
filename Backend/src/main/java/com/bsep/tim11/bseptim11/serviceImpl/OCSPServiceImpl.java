package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.ResponseCertificateDTO;
import com.bsep.tim11.bseptim11.dto.RevokeCertificateDTO;
import com.bsep.tim11.bseptim11.enumeration.CertificateRole;
import com.bsep.tim11.bseptim11.enumeration.CertificateStatus;
import com.bsep.tim11.bseptim11.exceptions.InvalidOCSPDataException;
import com.bsep.tim11.bseptim11.model.OCSPItem;
import com.bsep.tim11.bseptim11.repository.OCSPRepository;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import com.bsep.tim11.bseptim11.service.OCSPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Service
public class OCSPServiceImpl implements OCSPService {

    @Autowired
    private OCSPRepository ocspRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Override
    public CertificateStatus checkStatus(String serialNumber) {
        OCSPItem ocspItem = ocspRepository.findBySerialNumber(serialNumber);
        if (ocspItem == null) {
            return CertificateStatus.GOOD;
        } else {
            return CertificateStatus.REVOKED;
        }
    }

    @Override
    public ResponseCertificateDTO revoke(RevokeCertificateDTO revokeCertificateDTO) throws
            NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {

        BigInteger serialNumber = new BigInteger(revokeCertificateDTO.getSerialNumber());
        CertificateRole certificateRole = revokeCertificateDTO.certRoleToEnum();

        if (ocspRepository.findBySerialNumber(revokeCertificateDTO.getSerialNumber()) != null) {
            throw new InvalidOCSPDataException("Certificate is already revoked.");
        }

        switch (certificateRole) {
            case END_ENTITY: {
                X509Certificate certificate = getCertificate(
                    serialNumber,
                    certificateRole,
                    revokeCertificateDTO.getEndEntityKeyStorePass(),
                    revokeCertificateDTO.getAlias()
                );
                ocspRepository.save(new OCSPItem(revokeCertificateDTO.getSerialNumber()));
                return new ResponseCertificateDTO(certificate);
            }
            case INTERMEDIATE: {
                X509Certificate certificate = getCertificate(
                    serialNumber,
                    certificateRole,
                    revokeCertificateDTO.getIntermediateKeyStorePass(),
                    revokeCertificateDTO.getAlias()
                );
                ocspRepository.save(new OCSPItem(revokeCertificateDTO.getSerialNumber()));
                revokeChildren(CertificateRole.END_ENTITY, certificate, revokeCertificateDTO.getEndEntityKeyStorePass());
                revokeChildren(CertificateRole.INTERMEDIATE, certificate, revokeCertificateDTO.getIntermediateKeyStorePass());
                return new ResponseCertificateDTO(certificate);
            }
            case ROOT: {
                X509Certificate certificate = getCertificate(
                    serialNumber,
                    certificateRole,
                    revokeCertificateDTO.getRootKeyStorePass(),
                    revokeCertificateDTO.getAlias()
                );
                ocspRepository.save(new OCSPItem(revokeCertificateDTO.getSerialNumber()));
                revokeChildren(CertificateRole.END_ENTITY, certificate, revokeCertificateDTO.getEndEntityKeyStorePass());
                revokeChildren(CertificateRole.INTERMEDIATE, certificate, revokeCertificateDTO.getIntermediateKeyStorePass());
                return new ResponseCertificateDTO(certificate);
            }
            default: {
                throw new InvalidOCSPDataException("Unsuccessful certificate revocation.");
            }
        }
    }

    @Override
    public boolean isRevoked(Certificate certificate) {
        OCSPItem ocspItem = ocspRepository.findBySerialNumber(((X509Certificate) certificate).getSerialNumber().toString());
        return ocspItem == null ? false : true;
    }

    private X509Certificate getCertificate(
            BigInteger serialNumber,
            CertificateRole certificateRole,
            String keyStorePassword,
            String alias) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {

        X509Certificate x509Certificate = (X509Certificate) keyStoreService.getCertificate(certificateRole, keyStorePassword, alias);
        if (x509Certificate == null || !x509Certificate.getSerialNumber().equals(serialNumber)) {
            throw new InvalidOCSPDataException("Non-existing certificate for revocation.");
        }
        return x509Certificate;
    }

    private void revokeChildren(CertificateRole certificateRole, Certificate certificate, String keyStorePass) throws
            CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {

        String keyStorePath = keyStoreService.getKeyStorePath(certificateRole);
        KeyStore keyStore = keyStoreService.getKeyStore(keyStorePath, keyStorePass);

        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate[] certificates = keyStore.getCertificateChain(alias);
            List<Certificate> certificateList = new ArrayList<>(Arrays.asList(certificates));

            if (certificateList.contains(certificate)) {
                for (Certificate cert : certificateList) {
                    String serialNumber = ((X509Certificate) cert).getSerialNumber().toString();

                    if (cert.equals(certificate)) {
                        break;
                    }

                    if (ocspRepository.findBySerialNumber(serialNumber) == null) {
                        ocspRepository.save(new OCSPItem(serialNumber));
                    }
                }
            }
        }
    }

}
