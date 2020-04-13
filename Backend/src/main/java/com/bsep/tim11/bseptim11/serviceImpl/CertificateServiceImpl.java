package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.*;
import com.bsep.tim11.bseptim11.enumeration.CertificateRole;
import com.bsep.tim11.bseptim11.exceptions.InvalidCertificateDataException;
import com.bsep.tim11.bseptim11.model.Entity;
import com.bsep.tim11.bseptim11.model.IssuerData;
import com.bsep.tim11.bseptim11.model.SubjectData;
import com.bsep.tim11.bseptim11.repository.EntityRepository;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import com.bsep.tim11.bseptim11.service.ValidationService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.ietf.jgss.GSSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final String SECURITY_FILES_PATH =
            "src" + File.separator +
            "main" + File.separator +
            "resources" + File.separator +
            "data" + File.separator;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private ValidationService validationService;

    @Override
    public ResponseCertificateDTO createSelfSigned(NewCertificateDTO newCertificateDTO) throws
            ParseException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
            IOException, CertificateException, OperatorCreationException, GSSException, KeyStoreException {

        CertificateDTO certificateDTO = newCertificateDTO.getCertificateData();
        Entity subject = entityRepository.findByCommonName(certificateDTO.getSubject().getCommonName());

        Date validFrom = getDate(certificateDTO.getValidFrom());
        Date validTo = getDate(certificateDTO.getValidTo());
        Date today = getToday();

        if (validFrom.before(today) || validFrom.after(validTo)) {
            throw new InvalidCertificateDataException("The certificate validity period is out of date.");
        }
        if (!certificateDTO.getKeyUsage().isEnabled()) {
            throw new InvalidCertificateDataException(("You have to select at least one key usage."));
        }
        if (!certificateDTO.getExtendedKeyUsage().isEnabled()) {
            throw new InvalidCertificateDataException("You have to select at least one extended key usage.");
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
                new Certificate[]{x509Certificate}
        );

        subject.setNumberOfRootCertificates(subject.getNumberOfRootCertificates() + 1);
        entityRepository.save(subject);

        return new ResponseCertificateDTO(x509Certificate);
    }

    @Override
    public ResponseCertificateDTO create(NewCertificateDTO newCertificateDTO) throws
            ParseException, InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, GSSException,
            OperatorCreationException {

        CertificateDTO certificateDTO = newCertificateDTO.getCertificateData();
        Entity subject = entityRepository.findByCommonName(certificateDTO.getSubject().getCommonName());

        CertificateDTO issuerCertificate = newCertificateDTO.getIssuerCertificate();
        Entity issuer = entityRepository.findByCommonName(issuerCertificate.getSubject().getCommonName());

        if (subject.getCommonName().equals(issuer.getCommonName())) {
            throw new InvalidCertificateDataException("Issuer and subject cannot be the same entity.");
        }

        Date validFrom = getDate(certificateDTO.getValidFrom());
        Date validTo = getDate(certificateDTO.getValidTo());

        if (validFrom.before(getToday()) || validFrom.after(validTo)) {
            throw new InvalidCertificateDataException("Certificate validity period is out of date.");
        }
        if (!certificateDataIsValid(certificateDTO, issuerCertificate, validFrom, validTo)) {
            throw new InvalidCertificateDataException("Certificate is invalid.");
        }

        SubjectData subjectData = getSubject(subject);
        String issuerKeyStorePassword = newCertificateDTO.getIssuerKeyStorePassword();
        IssuerData issuerData = getIssuer(
            issuer,
            issuerCertificate,
            issuerKeyStorePassword,
            newCertificateDTO.getIssuerPrivateKeyPassword()
        );

        CertificateRole certificateRole = getCertificateRole(issuerCertificate);

        X509Certificate x509Certificate = generateCertificate(
            subjectData,
            issuerData,
            validFrom,
            validTo,
            certificateDTO
        );

        Certificate[] certificates = getCertificateChain(
            certificateRole,
            issuerKeyStorePassword,
            issuerCertificate.getAlias(),
            x509Certificate
        );

        keyStoreService.store(
            newCertificateDTO.getKeyStorePassword(),
            newCertificateDTO.getAlias(),
            subjectData.getPrivateKey(),
            newCertificateDTO.getPrivateKeyPassword(),
            certificates
        );

        return new ResponseCertificateDTO(x509Certificate);
    }

    @Override
    public void download(DownloadCertificateDTO downloadCertificateDTO) throws
            CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {

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

        FileOutputStream fos = new FileOutputStream(SECURITY_FILES_PATH + certificateRoleStr + "_" + alias + ".cer");
        fos.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
        fos.write(Base64.encodeBase64(certificate.getEncoded(), true));
        fos.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        fos.close();
    }

    private Certificate[] getCertificateChain(CertificateRole certificateRole, String issuerKeyStorePassword,
                                              String alias, Certificate certificate) throws
            CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException {

        Certificate[] certificates = keyStoreService.getCertificateChain(
            certificateRole,
            issuerKeyStorePassword,
            alias
        );

        if (!validationService.validate(certificates)) {
            throw new CertificateException("Issuer's certificate is invalid.");
        }

        List<Certificate> certificateList = new ArrayList<>(Arrays.asList(certificates));
        certificateList.add(0, certificate);

        Certificate[] newCertificates = new Certificate[certificateList.size()];
        for (int i = 0; i < certificateList.size(); i++) {
            newCertificates[i] = certificateList.get(i);
        }

        return newCertificates;
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

    private SubjectData getSubject(Entity entity) throws
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

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

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp;
        ecsp = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecsp);
        return keyPairGenerator.generateKeyPair();
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
        CertificateDTO certificateDTO
    ) throws CertIOException, OperatorCreationException, GSSException, CertificateException {

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
                    false,
                    new AuthorityKeyIdentifier(issuerData.getPublicKey().getEncoded())
            );
        }
        if (certificateDTO.getSubjectKeyIdentifier()) {
            x509v3CertificateBuilder.addExtension(
                    Extension.subjectKeyIdentifier,
                    false,
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
            x509v3CertificateBuilder.addExtension(Extension.keyUsage, false, keyUsage);
        }
        if (certificateDTO.getExtendedKeyUsage().isEnabled()) {
            ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(
                certificateDTO.getExtendedKeyUsage().keyPurposeIds()
            );
            x509v3CertificateBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);
        }

        String url = "http://localhost:8080/api/ocsp?sn=" + serialNumber.toString();
        GeneralName generalName = new GeneralName(GeneralName.uniformResourceIdentifier, url);

        AuthorityInformationAccess authorityInformationAccess = new AuthorityInformationAccess(
            new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.1"), generalName
        );
        x509v3CertificateBuilder.addExtension(
            Extension.authorityInfoAccess,
            false,
            authorityInformationAccess
        );

        return new JcaX509CertificateConverter().setProvider("BC")
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
            throw new InvalidCertificateDataException("Validity period of the certificate must be within validity" +
                    "period of the issuer's certificate, which is from " + formatDate(validFrom) + " to " +
                    formatDate(validTo));
        }

        if (newCertificateDTO.getSubjectIsCa() && !newCertificateDTO.getKeyUsage().isEnabled()) {
            throw new InvalidCertificateDataException("You have to select at least one key usage.");
        }

        if (newCertificateDTO.getSubjectIsCa() && !newCertificateDTO.getExtendedKeyUsage().isEnabled()) {
            throw new InvalidCertificateDataException("You have to select at least one extended key usage.");
        }

        if (issuerCertificate.getKeyUsage() != null && newCertificateDTO.getKeyUsage() != null) {
            List<Integer> subjectFalseKeyUsages = newCertificateDTO.getKeyUsage().falseKeyUsageIdentifiers();
            List<Integer> issuerFalseKeyUsages = issuerCertificate.getKeyUsage().falseKeyUsageIdentifiers();
            for (Integer identifier : issuerFalseKeyUsages) {
                if (!subjectFalseKeyUsages.contains(identifier)) {
                    throw new InvalidCertificateDataException("Some of the selected key usages cannot be set" +
                            "because the issuer's certificate cannot sign them.");
                }
            }
        }

        if (issuerCertificate.getExtendedKeyUsage() != null && newCertificateDTO.getExtendedKeyUsage() != null) {
            List<KeyPurposeId> subjectFalseExtendedKeyUsages = newCertificateDTO.getExtendedKeyUsage().falseExtendedKeyUsageIdentifiers();
            List<KeyPurposeId> issuerFalseExtendedKeyUsages = issuerCertificate.getExtendedKeyUsage().falseExtendedKeyUsageIdentifiers();
            for (KeyPurposeId identifier : issuerFalseExtendedKeyUsages) {
                if (!subjectFalseExtendedKeyUsages.contains(identifier)) {
                    throw new InvalidCertificateDataException("Some of the selected extended key usages cannot be " +
                            "set because the issuer's certificate cannot sign them.");
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
        CertificateRole certificateRole = getCertificateRole(issuerCertificate);

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

    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy.");
        return simpleDateFormat.format(date);
    }

    private CertificateRole getCertificateRole(CertificateDTO certificateDTO) {
        CertificateRole certificateRole = CertificateRole.INTERMEDIATE;
        if (certificateDTO.getSubject().getCommonName().equals(certificateDTO.getIssuer().getCommonName())) {
            certificateRole = CertificateRole.ROOT;
        }
        return certificateRole;
    }

    public static Date getToday() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

}
