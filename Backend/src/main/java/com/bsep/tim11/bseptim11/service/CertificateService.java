package com.bsep.tim11.bseptim11.service;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.enums.CertificateType;
import com.bsep.tim11.bseptim11.keystores.KeyStoreReader;
import com.bsep.tim11.bseptim11.keystores.KeyStoreWriter;
import com.bsep.tim11.bseptim11.model.Certificate;
import com.bsep.tim11.bseptim11.model.IssuerData;
import com.bsep.tim11.bseptim11.model.Subject;
import com.bsep.tim11.bseptim11.model.SubjectData;
import com.bsep.tim11.bseptim11.repository.CertificateRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

	private KeyStoreReader keyStoreReader;
	
	private KeyStoreWriter keyStoreWriter;
	
	private KeyStore keyStore;

	@Autowired
	private CertificateRepository certificateRepository;
	
	public CertificateService() {}
	
	public Certificate findOne(Long id) {
		return certificateRepository.findOneById(id);
	}
	
	public List<Certificate> findAll(){
		return certificateRepository.findAll();
	}
	
	public Certificate save(Certificate certificate) {
		return certificateRepository.save(certificate);
	}
	
	public KeyPair generateKeyPair() {
        try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); 
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(2048, random);
			return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public SubjectData generateSubjectData(Subject subject) {
		try {
			KeyPair keyPairSubject = generateKeyPair();
			
			//Datumi od kad do kad vazi sertifikat
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2017-12-31");
			Date endDate = iso8601Formater.parse("2022-12-31");
			
			//Serijski broj sertifikata
			String sn="1";
			//klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		    builder.addRDN(BCStyle.CN, subject.getCommonName());
		    builder.addRDN(BCStyle.SURNAME, subject.getSurname());
		    builder.addRDN(BCStyle.GIVENNAME, subject.getGivenName());
		    builder.addRDN(BCStyle.O, subject.getOrganization());
		    builder.addRDN(BCStyle.OU, subject.getOrganizationUnit());
		    builder.addRDN(BCStyle.C, subject.getCountry());
		    builder.addRDN(BCStyle.E, subject.getEmail());
		    //UID (USER ID) je ID korisnika
		    builder.addRDN(BCStyle.UID, "12345");
		    
		    //Kreiraju se podaci za sertifikat, sto ukljucuje:
		    // - javni kljuc koji se vezuje za sertifikat
		    // - podatke o vlasniku
		    // - serijski broj sertifikata
		    // - od kada do kada vazi sertifikat
		    
		    SubjectData sd = new SubjectData(keyPairSubject.getPublic(), builder.build(), sn);

		    return sd;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public IssuerData generateIssuerData(PrivateKey issuerKey, Subject subject) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, subject.getCommonName());
		builder.addRDN(BCStyle.SURNAME, subject.getSurname());
		builder.addRDN(BCStyle.GIVENNAME, subject.getGivenName());
		builder.addRDN(BCStyle.O, subject.getOrganization());
		builder.addRDN(BCStyle.OU, subject.getOrganizationUnit());
		builder.addRDN(BCStyle.C, subject.getCountry());
		builder.addRDN(BCStyle.E, subject.getEmail());
		builder.addRDN(BCStyle.UID, "12345");
	
		return new IssuerData(issuerKey, builder.build());
	}
	
	
	public X509Certificate createCertificate(String alias, String pass, X509Certificate cert, PrivateKey pk, CertificateType ct) throws IOException {

		this.keyStoreWriter = new KeyStoreWriter(); 
		
		this.keyStore = keyStoreWriter.getKeyStore();
				
		//BufferedInputStream in = new BufferedInputStream(new FileInputStream("/data/keystore.p12"));
		char[] password = "123".toCharArray();
		
		// Ovu liniju pisemo u if-u kada prvi put pravimo keystore (kada jos ne postoje)
		//keyStoreWriter.loadKeyStore(null, password);
		
		if(ct.equals(CertificateType.ROOT)) {
			keyStoreWriter.loadKeyStore("keystoreroot.p12", password);
			keyStoreWriter.saveKeyStore("keystoreroot.p12", password);
			keyStoreWriter.write(alias, pk, pass.toCharArray(), cert);
			keyStoreWriter.saveKeyStore("keystoreroot.p12", password);
			// Za sad sve trpam u ovaj dok ne razdvojimo intermediate i end-entity
		} else {
			keyStoreWriter.loadKeyStore("keystorenijeroot.p12", password);
			keyStoreWriter.saveKeyStore("keystorenijeroot.p12", password);
			keyStoreWriter.write(alias, pk, pass.toCharArray(), cert);
			keyStoreWriter.saveKeyStore("keystorenijeroot.p12", password);
		}
		

		//test
		this.keyStoreReader = new KeyStoreReader();
		
		cert = (X509Certificate) keyStoreReader.readCertificate("keystoreroot.p12", "123", "root1");
		if(cert != null) {
			System.out.println(cert);
		}else {
			System.out.println("NKEMA GAAAAAAAAAAAAA+++++++++++++++++++++");
		}
	
		
		return cert;
	}
	
	public Certificate convertFromDTO(CertificateDTO cDTO) {
		Certificate c = new Certificate(cDTO.getSubjectId(), cDTO.getIssuerId(), cDTO.getStartDate(), cDTO.getEndDate(), cDTO.getAlias());
		return c;
	}
}
