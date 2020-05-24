package com.bsep.tim11.bseptim11.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import com.bsep.tim11.bseptim11.certificates.CertificateGenerator;
import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.dto.SubjectDTO;
import com.bsep.tim11.bseptim11.enums.CertificateType;
import com.bsep.tim11.bseptim11.keystores.KeyStoreReader;
import com.bsep.tim11.bseptim11.model.*;
import com.bsep.tim11.bseptim11.service.AliasDataService;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.SubjectService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/certificate")
public class CertificateController {
	
	//@Autowired
	private KeyStoreReader keyStoreReader;
	@Autowired
	private SubjectService subjectService;
    private KeyStore keyStore;

	@Autowired
	private CertificateService certificateService;
	@Autowired
	private AliasDataService aliasDataService;
	
	//Vraca sve subjekte koji mogu biti issueri, samo one koji su CA
	@GetMapping(value = "/issuers")
	public ResponseEntity<List<SubjectDTO>> getIssuers(){
		
		List<SubjectDTO> issuersDTO = new ArrayList<>();
		List<Subject> issuers = subjectService.findAll();
		
		for (Subject issuer : issuers) {
			if(issuer.getIsCA())
				issuersDTO.add(new SubjectDTO(issuer));
		}
		
		return new ResponseEntity<>(issuersDTO, HttpStatus.OK);
		
	}
	
	//Svi ponudjeni subjecti za sertifikat
	@GetMapping(value = "/subjects")
	public ResponseEntity<List<SubjectDTO>> getSubjects(){
		
		List<SubjectDTO> subjectsDTO = new ArrayList<>();
		List<Subject> subjects = subjectService.findAll();
		
		for (Subject subject : subjects) {
			if(!subject.getHasCertificate())
				subjectsDTO.add(new SubjectDTO(subject));
		}
		
		return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
		
	}
	
	//Pravljenje self-signed sertifikata
	@PostMapping(value = "/createRoot", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateDTO> addRootCertificate(@RequestBody CertificateDTO certificateDTO){
		
		//Ovo ne znam gde da stavim, ima u primeru sa vezbi
		Security.addProvider(new BouncyCastleProvider());
		
		//Da znamo u koji keystore da ga bacimo
		CertificateType ct = CertificateType.ROOT;
		
		if(certificateDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		Subject subject = subjectService.findOne((Long)certificateDTO.getSubjectId());
		
		if(subject == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		subject.setIsCA(true);
		subject.setHasCertificate(true);
		subjectService.save(subject);
		
		Subject issuer = subjectService.findOne((Long)certificateDTO.getIssuerId());
		
		KeyPair keyPair = certificateService.generateKeyPair();
		
		SubjectData subjectData = certificateService.generateSubjectData(subject);
		IssuerData issuerData = certificateService.generateIssuerData(keyPair.getPrivate(), issuer);
		
		CertificateGenerator cg = new CertificateGenerator();
		X509Certificate cert = cg.generateCertificate(subjectData, issuerData, certificateDTO);

		try {
			certificateService.createCertificate(certificateDTO.getAlias(), certificateDTO.getPassword(),  cert, issuerData.getPrivateKey(), ct);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Certificate c = certificateService.convertFromDTO(certificateDTO);
		c.setIsRevoked(false);
		certificateService.save(c);
		
		AliasData ad= new AliasData();
		System.out.println("Iz nekog razloga ulazis ovde?????");
		ad.setId(22222L);
		ad.setAlias(certificateDTO.getAlias());
		aliasDataService.save(ad);
		
		return new ResponseEntity<>(certificateDTO, HttpStatus.CREATED);
		
	}
	
	//Pravljenje potpisanih sertifikata
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateDTO> addCertificate(@RequestBody CertificateDTO certificateDTO){
		
		Security.addProvider(new BouncyCastleProvider());
		
		//Da znamo u koji keystore da ga bacimo
		CertificateType ct = CertificateType.INTERMEDIATE;
		
		if(certificateDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		Subject subject = subjectService.findOne((Long)certificateDTO.getSubjectId());
		
		if(subject == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		subject.setIsCA(false);
		subject.setHasCertificate(true);
		subjectService.save(subject);
		
		Subject issuer = subjectService.findOne((Long)certificateDTO.getIssuerId());
		
		KeyPair keyPair = certificateService.generateKeyPair();
		
		SubjectData subjectData = certificateService.generateSubjectData(subject);
		IssuerData issuerData = certificateService.generateIssuerData(keyPair.getPrivate(), issuer);
		
		CertificateGenerator cg = new CertificateGenerator();
		X509Certificate cert = cg.generateCertificate(subjectData, issuerData, certificateDTO);

		try {
			certificateService.createCertificate(certificateDTO.getAlias(), certificateDTO.getPassword(), cert, issuerData.getPrivateKey(), ct);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Certificate c = certificateService.convertFromDTO(certificateDTO);
		c.setIsRevoked(false);
		AliasData ad= new AliasData();
		ad.setId(null);
		ad.setAlias(certificateDTO.getAlias());
		
		this.keyStoreReader = new KeyStoreReader();
		
		List<AliasData> aliasDatas = aliasDataService.findAll();
		for (AliasData ad2 : aliasDatas){
			if(ad2.getId() == certificateDTO.getIssuerId()){
			cert = (X509Certificate) keyStoreReader.readCertificate("keystoreroot.p12", "123", ad2.getAlias());
			if (cert != null) {
				System.out.println("Udjes u root?");
				
				ad.setAliasData(ad2);
				aliasDataService.save(ad);
				break;
			}
			else{
				System.out.println("Udjes u else?");
				cert = (X509Certificate) keyStoreReader.readCertificate("keystorenijeroot.p12", "123", ad2.getAlias());
				if (cert != null) {
					ad.setAliasData(ad2);
					aliasDataService.save(ad);
					break;
				}
			}
			}
			ad2.getAliases().add(ad);
		}
		//aliasDataService.save(ad);
		certificateService.save(c);
		
		return new ResponseEntity<>(certificateDTO, HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateDTO> revokeCertificate(@RequestBody CertificateDTO certificateDTO) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		
		Security.addProvider(new BouncyCastleProvider());
		
		//Da znamo u koji keystore da ga bacimo
		CertificateType ct = CertificateType.ROOT;
		
		if(certificateDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	//	KeyStore ks;
		
	//		 ks = KeyStore.getInstance(KeyStore.getDefaultType());
		
	/*	try {
			ks.load(new FileInputStream(new File("keystoreroot.p12")), "123".toCharArray());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		List<AliasData> aliasDatas = aliasDataService.findAll();
		for(AliasData ad : aliasDatas){
			if(ad.getAlias().equals(certificateDTO.getAlias())){
				certificateService.findOne(ad.getId()).setIsRevoked(true);
				if(!ad.getAliases().isEmpty()){
					for(AliasData ad2 : ad.getAliases()){
						
						removeFromBase(ad2);
					}
				}
			}
		}
		//ks.deleteEntry(certificateDTO.getAlias());
		//ks.store(new FileOutputStream(new File("keystoreroot.p12")), "123".toCharArray());

		return new ResponseEntity<>(certificateDTO, HttpStatus.GONE);
	}
	public void removeFromBase(AliasData ad){
		certificateService.findOne(ad.getId()).setIsRevoked(true);
		if(!ad.getAliases().isEmpty()){
			for(AliasData ad2 : ad.getAliases()){
				removeFromBase(ad2);
			}
		}
		
		AliasData adZaBrisanje = aliasDataService.findOne(ad.getId());
		if(adZaBrisanje != null){
		//	aliasDataService.remove(ad.getId());
			aliasDataService.save(adZaBrisanje);
		}
	}
	@GetMapping(value = "/isValid", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isItAValidCertificate(@RequestBody CertificateDTO certificateDTO) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		Certificate c = certificateService.convertFromDTO(certificateDTO);
		return new ResponseEntity<>(c.isValid(), HttpStatus.OK);
	}
	
	
}
