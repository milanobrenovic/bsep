package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.*;
import com.bsep.tim11.bseptim11.enumeration.CertificateRole;
import com.bsep.tim11.bseptim11.model.Entity;
import com.bsep.tim11.bseptim11.model.IssuerData;
import com.bsep.tim11.bseptim11.model.SubjectData;
import com.bsep.tim11.bseptim11.repository.EntityRepository;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Override
    public ExistingCertificateDTO create(NewCertificateDTO newCertificateDTO) throws Exception {
        CertificateDTO certificateDTO = newCertificateDTO.getCertificateData();
        Entity subject = entityRepository.findByCommonName(certificateDTO.getSubject().getCommonName());

        CertificateDTO issuerCertificate = newCertificateDTO.getIssuerCertificate();
        Entity issuer = entityRepository.findByCommonName(issuerCertificate.getIssuer().getCommonName());

        if (subject.getCommonName().equals(issuer.getCommonName())) {
            throw new IllegalArgumentException("Issuer and subject cannot be the same entity.");
        }

        Date validFrom = getDate(certificateDTO.getValidFrom());
        Date validTo = getDate(certificateDTO.getValidTo());

        if (validFrom.before(new Date()) || validFrom.after(validTo)) {
            throw new IllegalArgumentException("Certificate validity period is out of date.");
        }

        if (!certificateDataIsValid(certificateDTO, issuerCertificate, validFrom, validTo)) {
            throw new IllegalArgumentException("Certificate is invalid.");
        }

        SubjectData subjectData = getSubject(subject);
        IssuerData issuerData = getIssuer(
            issuer,
            issuerCertificate,
            newCertificateDTO.getIssuerKeyStorePassword(),
            newCertificateDTO.getIssuerPrivateKeyPassword()
        );

        X509Certificate x509Certificate = generateCertificate(
            subjectData,
            issuerData,
            validFrom,
            validTo,
            certificateDTO
        );

        keyStoreService.store(
            newCertificateDTO.getKeyStorePassword(),
            newCertificateDTO.getAlias(),
            subjectData.getPrivateKey(),
            newCertificateDTO.getPrivateKeyPassword(),
            x509Certificate
        );

        return new ExistingCertificateDTO(x509Certificate);
    }

    @Override
    public ExistingCertificateDTO createSelfSigned(NewCertificateDTO newCertificateDTO) throws Exception {
        CertificateDTO certificateDTO = newCertificateDTO.getCertificateData();
        Entity subject = entityRepository.findByCommonName(certificateDTO.getSubject().getCommonName());

        Date validFrom = getDate(certificateDTO.getValidFrom());
        Date validTo = getDate(certificateDTO.getValidTo());

        if (validFrom.before(new Date()) || validFrom.after(validTo)) {
            throw new BadCredentialsException("The certificate validity period is out of date.");
        }

        SubjectData subjectData = getSubject(subject);
        IssuerData issuerData = getIssuerForSelfSigned(subjectData);

        X509Certificate x509Certificate = generateCertificate(
            subjectData,
            issuerData,
            validFrom,
            validTo,
            certificateDTO
        );

        keyStoreService.store(
            newCertificateDTO.getKeyStorePassword(),
            newCertificateDTO.getAlias(),
            subjectData.getPrivateKey(),
            newCertificateDTO.getPrivateKeyPassword(),
            x509Certificate
        );
        entityRepository.save(subject);

        return new ExistingCertificateDTO(x509Certificate);
    }

    @Override
    public void download(DownloadCertificateDTO downloadCertificateDTO)
            throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {

        String certificateRoleStr = downloadCertificateDTO.getCertRole().toLowerCase();
        String alias = downloadCertificateDTO.getAlias();
        CertificateRole certificateRole = downloadCertificateDTO.returnCertRoleToEnum();

        if (certificateRole == null) {
            throw new NullPointerException("Certificate role has not been defined.");
        }

        Certificate certificate = keyStoreService.getCertificate(
            certificateRole,
            downloadCertificateDTO.getKeyStorePassword(),
            alias
        );

        FileOutputStream fos = new FileOutputStream(certificateRoleStr + "_" + alias + ".cer");
        fos.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
        fos.write(Base64.encodeBase64(certificate.getEncoded(), true));
        fos.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        fos.close();
    }

    private Date getDate(String date) throws ParseException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            return dateFormat.parse(date);
        }
    }

    private SubjectData getSubject(Entity entity) {
        KeyPair keyPairSubject = generateKeyPair();
        if (keyPairSubject == null) {
            return null;
        }
        X500Name x500Name = getX500Name(entity);
        return new SubjectData(
            keyPairSubject.getPublic(),
            x500Name,
            keyPairSubject.getPrivate(),
            entity.getId()
        );
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "SunEC");
            ECGenParameterSpec ecsp;
            ecsp = new ECGenParameterSpec("secp256k1");
            keyPairGenerator.initialize(ecsp);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X500Name getX500Name(Entity entity) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, entity.getCommonName());
        builder.addRDN(BCStyle.O, entity.getOrganization());
        builder.addRDN(BCStyle.C, entity.getCountryCode());

        if (entity.getType().toString().equals("USER")) {
            builder.addRDN(BCStyle.SURNAME, entity.getSurname());
            builder.addRDN(BCStyle.GIVENNAME, entity.getGivename());
            builder.addRDN(BCStyle.EmailAddress, entity.getEmail());
        } else {
            builder.addRDN(BCStyle.OU, entity.getOrganizationUnit());
        }

        return builder.build();
    }

    private IssuerData getIssuerForSelfSigned(SubjectData subjectData) {
        return new IssuerData(
            subjectData.getPrivateKey(),
            subjectData.getX500name(),
            subjectData.getPublicKey(),
            subjectData.getId()
        );
    }

    private X509Certificate generateCertificate(
        SubjectData subjectData,
        IssuerData issuerData,
        Date validFrom,
        Date validTo,
        CertificateDTO certificateDTO)
        throws Exception {

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withECDSA");
        builder = builder.setProvider("BC");
        ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
        BigInteger serialNumber = new BigInteger(1, getSerialNumber());
        X509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
            issuerData.getX500name(),
            serialNumber,
            validFrom,
            validTo,
            subjectData.getX500name(),
            subjectData.getPublicKey()
        );
        x509v3CertificateBuilder.setSubjectUniqueID(toBoolArray(subjectData.getId()));
        x509v3CertificateBuilder.setIssuerUniqueID(toBoolArray(issuerData.getId()));

        if (certificateDTO.getAuthorityKeyIdentifier()) {
            x509v3CertificateBuilder.addExtension(
                    Extension.authorityKeyIdentifier,
                    true,
                    new AuthorityKeyIdentifier(issuerData.getPublicKey().getEncoded())
            );
        }
        if (certificateDTO.getSubjectKeyIdentifier()) {
            x509v3CertificateBuilder.addExtension(
                    Extension.subjectKeyIdentifier,
                    true,
                    new SubjectKeyIdentifier(subjectData.getPublicKey().getEncoded())
            );
        }
        if (certificateDTO.getSubjectIsCa()) {
            x509v3CertificateBuilder.addExtension(
                    Extension.basicConstraints,
                    true,
                    new BasicConstraints(true)
            );
        }
        if (certificateDTO.getKeyUsage().isEnabled()) {
            KeyUsageDTO keyUsageDTO = certificateDTO.getKeyUsage();
            KeyUsage keyUsage = new KeyUsage(
                keyUsageDTO.getDigitalSignatureInt() |
                keyUsageDTO.getNonRepudiationInt() |
                keyUsageDTO.getKeyEnciphermentInt() |
                keyUsageDTO.getDataEnciphermentInt() |
                keyUsageDTO.getKeyAgreementInt() |
                keyUsageDTO.getCertificateSigningInt() |
                keyUsageDTO.getCrlSignInt() |
                keyUsageDTO.getEncipherOnlyInt() |
                keyUsageDTO.getDecipherOnlyInt()
            );
            x509v3CertificateBuilder.addExtension(Extension.keyUsage, true, keyUsage);
        }
        if (certificateDTO.getExtendedKeyUsage().isEnabled()) {
            ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(
                certificateDTO.getExtendedKeyUsage().keyPurposeIds()
            );
            x509v3CertificateBuilder.addExtension(Extension.extendedKeyUsage, true, extendedKeyUsage);
        }

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(x509v3CertificateBuilder.build(contentSigner));
    }

    private byte[] getSerialNumber() {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        return bytes;
    }

    private boolean[] toBoolArray(Long value) {
        char[] cArray = new char[7];
        Arrays.fill(cArray, '0');

        if (value != -1) {
            char[] chars = Long.toBinaryString(value).toCharArray();

            int offset = 0;
            if (chars.length < cArray.length) {
                offset = cArray.length - chars.length;
            }

            System.arraycopy(chars, 0, cArray, offset, chars.length);
        }

        boolean[] bits = new boolean[cArray.length];
        for (int i = 0; i < cArray.length; i++) {
            bits[i] = cArray[i] == '1';
        }

        return bits;
    }

    private boolean certificateDataIsValid(
        CertificateDTO newCertificateDTO,
        CertificateDTO issuerCertificate,
        Date validFrom,
        Date validTo)
        throws ParseException {

        if (validFrom.before(getDate(issuerCertificate.getValidFrom()))
            || validTo.after(getDate(issuerCertificate.getValidTo()))) {
            return false;
        }

        if (issuerCertificate.getKeyUsage() != null && newCertificateDTO.getKeyUsage() != null) {
            List<Integer> subjectFalseKeyUsages = newCertificateDTO.getKeyUsage().falseKeyUsageIdentifiers();
            List<Integer> issuerFalseKeyUsages = issuerCertificate.getKeyUsage().falseKeyUsageIdentifiers();
            for (Integer identifier : issuerFalseKeyUsages) {
                if (!subjectFalseKeyUsages.contains(identifier)) {
                    return false;
                }
            }
        }

        if (issuerCertificate.getExtendedKeyUsage() != null && newCertificateDTO.getExtendedKeyUsage() != null) {
            List<KeyPurposeId> subjectFalseExtendedKeyUsages = newCertificateDTO.getExtendedKeyUsage().falseExtendedKeyUsageIdentifiers();
            List<KeyPurposeId> issuerFalseExtendedKeyUsages = issuerCertificate.getExtendedKeyUsage().falseExtendedKeyUsageIdentifiers();
            for (KeyPurposeId identifier : issuerFalseExtendedKeyUsages) {
                if (!subjectFalseExtendedKeyUsages.contains(identifier)) {
                    return false;
                }
            }
        }

        return true;
    }

    private IssuerData getIssuer(
            Entity issuer,
            CertificateDTO issuerCertificate,
            String keyStorePassword,
            String issuerPrivateKeyPassword)
        throws UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {

        X500Name x500Name = getX500Name(issuer);
        CertificateRole certificateRole = CertificateRole.INTERMEDIATE;

        if (issuerCertificate.getSubject().getCommonName().equals(issuerCertificate.getIssuer().getCommonName())) {
            certificateRole = CertificateRole.ROOT;
        }

        PrivateKey privateKey = keyStoreService.getPrivateKey(
                certificateRole,
                keyStorePassword,
                issuerCertificate.getAlias(),
                issuerPrivateKeyPassword
        );
        PublicKey publicKey = keyStoreService.getPublicKey(
                certificateRole,
                keyStorePassword,
                issuerCertificate.getAlias()
        );

        return new IssuerData(privateKey, x500Name, publicKey, issuer.getId());
    }

}
