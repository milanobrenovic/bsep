package com.bsep.tim11.bseptim11.service;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.enums.CertificateType;
import com.bsep.tim11.bseptim11.keystores.KeyStoreReader;
import com.bsep.tim11.bseptim11.keystores.KeyStoreWriter;
import com.bsep.tim11.bseptim11.model.Certificate;
import com.bsep.tim11.bseptim11.model.IssuerData;
import com.bsep.tim11.bseptim11.model.Subject;
import com.bsep.tim11.bseptim11.model.SubjectData;
import com.bsep.tim11.bseptim11.repository.CertificateRepository;

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
		KeyPair keyPairSubject = generateKeyPair();
		
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
		
		if(ct.equals(CertificateType.ROOT)) {
			File f = new File("keystoreroot.p12");
			if(!f.isFile()) {
				// U if ulazi kada prvi put pravimo keystore odnosno kada on jos ne postoji
				keyStoreWriter.loadKeyStore(null, password);
			} else {
				// U else ulazi svaki sledeci put
				keyStoreWriter.loadKeyStore("keystoreroot.p12", password);
			}
			keyStoreWriter.write(alias, pk, pass.toCharArray(), cert);
			keyStoreWriter.saveKeyStore("keystoreroot.p12", password);
			// Intermediate keyStore
		} else if(ct.equals(CertificateType.INTERMEDIATE)){
			File f = new File("keystoreintermediate.p12");
			if(!f.isFile()) {
				keyStoreWriter.loadKeyStore(null, password);
			} else {
				keyStoreWriter.loadKeyStore("keystoreintermediate.p12", password);
			}
			keyStoreWriter.write(alias, pk, pass.toCharArray(), cert);
			keyStoreWriter.saveKeyStore("keystoreintermediate.p12", password);
		} else if(ct.equals(CertificateType.ENDENTITY)) {
			File f = new File("keystoreendentity.p12");
			if(!f.isFile()) {
				keyStoreWriter.loadKeyStore(null, password);
			} else {
				keyStoreWriter.loadKeyStore("keystoreendentity.p12", password);
			}
			keyStoreWriter.write(alias, pk, pass.toCharArray(), cert);
			keyStoreWriter.saveKeyStore("keystoreendentity.p12", password);
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
