package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.*;
import com.bsep.tim11.bseptim11.enumeration.CertificateRole;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import com.bsep.tim11.bseptim11.service.ValidationService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Service
public class KeyStoreServiceImpl implements KeyStoreService {

    private static final String SECURITY_FILES_PATH =
            "src" + File.separator +
            "main" + File.separator +
            "resources" + File.separator +
            "data" + File.separator;
    private final String ROOT_KEYSTORE = SECURITY_FILES_PATH + "root_keystore.p12";
    private final String INTERMEDIATE_KEYSTORE = SECURITY_FILES_PATH + "intermediate_keystore.p12";
    private final String END_ENTITY_KEYSTORE = SECURITY_FILES_PATH + "end_entity_keystore.pkcs12";

    @Autowired
    private ValidationService validationService;

    @Override
    public void store(
            String keyStorePassword,
            String alias,
            PrivateKey privateKey,
            String keyPassword,
            Certificate[] certificates)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        char[] keyPassArray = keyPassword.toCharArray();
        char[] keyStorePassArray = keyStorePassword.toCharArray();

        CertificateRole certificateRole = getCertificateRole(certificates[0]);
        String keyStorePath = getKeyStorePath(certificateRole);

        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        keyStore.setKeyEntry(alias, privateKey, keyPassArray, certificates);
        keyStore.store(new FileOutputStream(keyStorePath), keyStorePassArray);
    }

    @Override
    public PrivateKey getPrivateKey(
            CertificateRole certificateRole,
            String keyStorePassword,
            String alias,
            String keyPassword)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException,
            IOException {

        char[] keyPassArray = keyPassword.toCharArray();
        String keyStorePath = getKeyStorePath(certificateRole);

        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        return (PrivateKey) keyStore.getKey(alias, keyPassArray);
    }

    @Override
    public PublicKey getPublicKey(
            CertificateRole certificateRole,
            String keyStorePassword,
            String alias)
            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {

        String keyStorePath = getKeyStorePath(certificateRole);
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        Certificate certificate = keyStore.getCertificate(alias);

        return certificate.getPublicKey();
    }

    @Override
    public Certificate getCertificate(
            CertificateRole certificateRole,
            String keyStorePassword,
            String alias)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

        String keyStorePath = getKeyStorePath(certificateRole);
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        Certificate certificate = keyStore.getCertificate(alias);

        return certificate;
    }

    @Override
    public Certificate[] getCertificateChain(
            CertificateRole certificateRole,
            String keyStorePassword,
            String alias) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {

        String keyStorePath = getKeyStorePath(certificateRole);
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        Certificate[] certificates = keyStore.getCertificateChain(alias);

        return certificates;
    }

    @Override
    public List<CertificateItemDTO> getCertificates(
            String role,
            String keyStorePassword)
            throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException,
            OCSPException, OperatorCreationException {

        String keyStorePath = getKeyStorePath(getCertificateRole(role));
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);

        Enumeration<String> aliases = keyStore.aliases();
        List<CertificateItemDTO> certificateItems = new ArrayList<>();
        X509Certificate issuerCertificate = null;

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate[] certificateChain = keyStore.getCertificateChain(alias);

            if (certificateChain.length == 1) {
                issuerCertificate = (X509Certificate) certificateChain[0];
            } else {
                issuerCertificate = (X509Certificate) certificateChain[1];
            }

            CertificateItemDTO certificateItemDTO = createCertificateItemDTO(
                (X509Certificate) certificateChain[0],
                issuerCertificate,
                alias
            );
            certificateItems.add(certificateItemDTO);
        }

        return certificateItems;
    }

    @Override
    public List<CertificateDTO> getCACertificates(
            Long subjectId,
            String rootKeyStoragePassword,
            String intermediateKeyStoragePassword)
            throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {

        if ((rootKeyStoragePassword == null || rootKeyStoragePassword.isEmpty()) &&
                (intermediateKeyStoragePassword == null || intermediateKeyStoragePassword.isEmpty())) {
            throw new BadCredentialsException("Please enter at least one of the passwords.");
        }

        List<CertificateDTO> certificateDTOS = new ArrayList<>();
        if (rootKeyStoragePassword != null && !rootKeyStoragePassword.isEmpty()) {
            loadCertificates(subjectId, certificateDTOS, rootKeyStoragePassword, "root");
        }
        if (intermediateKeyStoragePassword != null && !intermediateKeyStoragePassword.isEmpty()) {
            loadCertificates(subjectId, certificateDTOS, intermediateKeyStoragePassword, "intermediate");
        }

        return certificateDTOS;
    }

    private CertificateRole getCertificateRole(String role) {
        if (role.equals("root")) {
            return CertificateRole.ROOT;
        } else if (role.equals("intermediate")) {
            return CertificateRole.INTERMEDIATE;
        } else {
            return CertificateRole.END_ENTITY;
        }
    }

    private CertificateRole getCertificateRole(Certificate certificate) throws CertificateEncodingException {
        X509Certificate tempCertificate = (X509Certificate) certificate;
        X500Name subject = new JcaX509CertificateHolder(tempCertificate).getSubject();
        X500Name issuer = new JcaX509CertificateHolder(tempCertificate).getIssuer();

        if (subject.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString()
                .equals(issuer.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString())) {
            return CertificateRole.ROOT;
        } else if (tempCertificate.getBasicConstraints() != -1) {
            // If the path length has been set, it's a CA certificate
            return CertificateRole.INTERMEDIATE;
        } else {
            return CertificateRole.END_ENTITY;
        }
    }

    @Override
    public String getKeyStorePath(CertificateRole certificateRole) {
        switch (certificateRole) {
            case ROOT:
                return ROOT_KEYSTORE;
            case INTERMEDIATE:
                return INTERMEDIATE_KEYSTORE;
            default:
                return END_ENTITY_KEYSTORE;
        }
    }

    @Override
    public KeyStore getKeyStore(String keyStorePath, String keyStorePassword)
        throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {

        char[] keyStorePassArray = keyStorePassword.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassArray);
        } catch (FileNotFoundException e) {
            keyStore.load(null, keyStorePassArray);
        }

        return keyStore;
    }

    private CertificateDTO createCertificateDTO(X509Certificate x509Certificate, String alias)
        throws CertificateParsingException, CertificateEncodingException {

        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setSerialNumber(x509Certificate.getSerialNumber().toString(16));

        X500Name x500Name = new JcaX509CertificateHolder(x509Certificate).getSubject();
        boolean[] subjectUniqueID = x509Certificate.getSubjectUniqueID();

        certificateDTO.setSubject(new EntityDTO(x500Name, boolArrayToLong(subjectUniqueID)));

        X500Name x500nameIssuer = new JcaX509CertificateHolder(x509Certificate).getIssuer();
        boolean[] issuerUniqueID = x509Certificate.getIssuerUniqueID();
        certificateDTO.setIssuer(new EntityDTO(x500nameIssuer, boolArrayToLong(issuerUniqueID)));

        // The method getExtensionValue() Gets the DER-encoded OCTET string for the extension
        // value (extnValue) identified by the passed-in OID String.
        // https://docs.oracle.com/javase/7/docs/api/java/security/cert/X509Extension.html#getExtensionValue
        if (x509Certificate.getExtensionValue("2.5.29.35") != null) {
            certificateDTO.setAuthorityKeyIdentifier(true);
        }
        if (x509Certificate.getExtensionValue("2.5.29.14") != null) {
            certificateDTO.setSubjectKeyIdentifier(true);
        }

        // Gets the certificate constraints path length from the critical
        // BasicConstraints extension, (OID = 2.5.29.19).
        if (x509Certificate.getBasicConstraints() != -1) {
            certificateDTO.setSubjectIsCa(true);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        certificateDTO.setValidFrom(dateFormat.format(x509Certificate.getNotBefore()));
        certificateDTO.setValidTo(dateFormat.format(x509Certificate.getNotAfter()));

        boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null) {
            certificateDTO.setKeyUsage(new KeyUsageDTO(x509Certificate.getKeyUsage()));
        }

        List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        if (extendedKeyUsage != null && !extendedKeyUsage.isEmpty()) {
            certificateDTO.setExtendedKeyUsage(new ExtendedKeyUsageDTO(extendedKeyUsage));
        }
        certificateDTO.setAlias(alias);

        return certificateDTO;
    }

    private CertificateItemDTO createCertificateItemDTO(
            X509Certificate x509Certificate,
            X509Certificate issuerCertificate,
            String alias
    ) throws CertificateEncodingException, OCSPException, OperatorCreationException,
            CertificateParsingException, IOException {

        CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        certificateItemDTO.setSerialNumber(x509Certificate.getSerialNumber().toString(16));

        X500Name x500Name = new JcaX509CertificateHolder(x509Certificate).getSubject();
        boolean[] subjectUniqueID = x509Certificate.getSubjectUniqueID();

        certificateItemDTO.setSubject(new EntityDTO(x500Name, boolArrayToLong(subjectUniqueID)));

        X500Name x500nameIssuer = new JcaX509CertificateHolder(x509Certificate).getIssuer();
        boolean[] issuerUniqueID = x509Certificate.getIssuerUniqueID();
        certificateItemDTO.setIssuer(new EntityDTO(x500nameIssuer, boolArrayToLong(issuerUniqueID)));

        if (x509Certificate.getBasicConstraints() != -1) {
            certificateItemDTO.setSubjectIsCa(true);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        certificateItemDTO.setValidFrom(simpleDateFormat.format(x509Certificate.getNotBefore()));
        certificateItemDTO.setValidTo(simpleDateFormat.format(x509Certificate.getNotAfter()));

        boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null) {
            certificateItemDTO.setKeyUsage(new KeyUsageDTO(x509Certificate.getKeyUsage()));
        }

        List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        if (extendedKeyUsage != null && !extendedKeyUsage.isEmpty()) {
            certificateItemDTO.setExtendedKeyUsage(new ExtendedKeyUsageDTO(extendedKeyUsage));
        }
        certificateItemDTO.setAlias(alias);

        X509CertificateHolder certificateHolder = new X509CertificateHolder(issuerCertificate.getEncoded());
        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
        DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);
        CertificateID certificateID = new CertificateID(
            digestCalculator,
            certificateHolder,
            x509Certificate.getSerialNumber()
        );
        certificateItemDTO.setCertificateIdentifier(new CertificateIdentifierDTO(certificateID));

        return certificateItemDTO;
    }

    private long boolArrayToLong(boolean[] attributes) {
        char[] chars = new char[7];
        Arrays.fill(chars, '0');

        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i]) {
                    chars[i] = '1';
                }
            }
        }

        return new BigInteger(new String(chars), 2).longValue();
    }

    private void loadCertificates(
            Long subjectId,
            List<CertificateDTO> certificateDTOS,
            String keyStorePassword,
            String role)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {

        String keyStorePath = getKeyStorePath(getCertificateRole(role));
        char[] keyStorePassArray = keyStorePassword.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassArray);
        } catch (FileNotFoundException e) {
            return;
        }

        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate[] certificatesChain = keyStore.getCertificateChain(alias);

            X509Certificate x509Certificate = (X509Certificate) certificatesChain[0];
            Long subjectUniqueID = boolArrayToLong(x509Certificate.getSubjectUniqueID());

            if (!subjectUniqueID.equals(subjectId) && validationService.validate(certificatesChain)) {
                certificateDTOS.add(createCertificateDTO(x509Certificate, alias));
            }
        }
    }

}
