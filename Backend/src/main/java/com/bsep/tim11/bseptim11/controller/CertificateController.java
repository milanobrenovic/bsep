package com.bsep.tim11.bseptim11.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.websocket.server.PathParam;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsep.tim11.bseptim11.certificates.CertificateGenerator;
import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.dto.CertificateDetailsDTO;
import com.bsep.tim11.bseptim11.dto.SubjectDTO;
import com.bsep.tim11.bseptim11.enums.CertificateType;
import com.bsep.tim11.bseptim11.keystores.KeyStoreReader;
import com.bsep.tim11.bseptim11.model.AliasData;
import com.bsep.tim11.bseptim11.model.Certificate;
import com.bsep.tim11.bseptim11.model.IssuerData;
import com.bsep.tim11.bseptim11.model.Subject;
import com.bsep.tim11.bseptim11.model.SubjectData;
import com.bsep.tim11.bseptim11.service.AliasDataService;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.SubjectService;

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
	
	@GetMapping(value = "/issuersKeyStore/{password}")
	public ResponseEntity<List<Subject>> getIssuersKeyStore(@PathVariable (value = "password")String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
		System.out.println("Password::: "+password);
		//List<SubjectDTO> issuersDTO = new ArrayList<>();
		if(!checkForInvalidInput(password)){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		List<Subject> issuers = new ArrayList<>();
		FileInputStream is = new FileInputStream("keystoreroot.p12");

	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(is, password.toCharArray());
	    Enumeration e = keystore.aliases();
	    for (; e.hasMoreElements();) {
	      String alias = (String) e.nextElement();

	     java.security.cert.Certificate cert = keystore.getCertificate(alias);
	      if (cert instanceof X509Certificate) {
	        X509Certificate x509cert = (X509Certificate) cert;

	        // Get subject
	        Principal principal = x509cert.getSubjectDN();
	        String subjectDn = principal.getName();

	        // Get issuer
		  //UID=12345, EMAILADDRESS=a@a.com, C=aa, OU=a, O=a, GIVENNAME=a, SURNAME=a, CN=a
	       // principal = x509cert.getIssuerDN();
	        String issuerDn = principal.getName();
	        String issuerData[] = issuerDn.split(" ");
	        String iData[] = issuerData[1].split("=");

			  String iFinalno[] = iData[1].split(",");
			 // System.out.println(iFinalno[0]);
//			  if(subjectService.findByEmail(iFinalno[0]) != null)
			  Boolean isCA = false;
			  Subject issuer = subjectService.findByEmail(iFinalno[0]);
			  List<AliasData> aliasDatas = aliasDataService.findAll();
			  for(AliasData add : aliasDatas){
					Certificate c = certificateService.findByAlias(add.getAlias());
					if(c.getIssuerId() == issuer.getId()){
						if(c.getIsRevoked()==false){
						isCA = true;
						}
					}
			}
			  if(isCA == true)
			  	issuers.add(issuer);
	      }
	    }
		File exits = new File("keystoreintermediate.p12");
	    if (exits.exists()){
		FileInputStream is2 = new FileInputStream("keystoreintermediate.p12");

		KeyStore keystore2 = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(is2, password.toCharArray());
		Enumeration e2 = keystore.aliases();
		for (; e2.hasMoreElements();) {
			String alias = (String) e2.nextElement();

			java.security.cert.Certificate cert = keystore.getCertificate(alias);
			if (cert instanceof X509Certificate) {
				X509Certificate x509cert = (X509Certificate) cert;

				// Get subject
				Principal principal = x509cert.getSubjectDN();
				String subjectDn = principal.getName();

				// Get issuer
				//UID=12345, EMAILADDRESS=a@a.com, C=aa, OU=a, O=a, GIVENNAME=a, SURNAME=a, CN=a
				//principal = x509cert.getIssuerDN();
				String issuerDn = principal.getName();
				String issuerData[] = issuerDn.split(" ");
				String iData[] = issuerData[1].split("=");

				String iFinalno[] = iData[1].split(",");
				System.out.println(iFinalno[0]);
//			  if(subjectService.findByEmail(iFinalno[0]) != null)
				 Boolean isCA = false;
				  Subject issuer = subjectService.findByEmail(iFinalno[0]);
				  List<AliasData> aliasDatas = aliasDataService.findAll();
				  for(AliasData add : aliasDatas){
						Certificate c = certificateService.findByAlias(add.getAlias());
				//		System.out.println("c: "+ c.getIssuerId()+"issuer: "+issuer.getId());
						if(c.getSubjectId() == issuer.getId()){
							if(c.getIsRevoked()==false){
							isCA = true;
							}
						}
				}
				  if(isCA == true)
				  	issuers.add(issuer);
		      }
			}
		}
		
		return new ResponseEntity<>(issuers, HttpStatus.OK);
		
	}
	
	//Pravljenje self-signed sertifikata
	@PostMapping(value = "/createRoot", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateDTO> addRootCertificate(@RequestBody CertificateDTO certificateDTO){
		if(!checkForInvalidInput(certificateDTO.getKeyStorePassword())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getPassword())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getAlias())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		if(aliasDataService.findByAlias(certificateDTO.getAlias())!=null){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
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
		
	/*	subject.setIsCA(true);
		subject.setHasCertificate(true);
		subjectService.save(subject);
		*/
		Subject issuer = subjectService.findOne((Long)certificateDTO.getIssuerId());
		
		KeyPair keyPair = certificateService.generateKeyPair();
		
		SubjectData subjectData = certificateService.generateSubjectData(subject);
		IssuerData issuerData = certificateService.generateIssuerData(keyPair.getPrivate(), issuer);
		
		CertificateGenerator cg = new CertificateGenerator();
		try {
			System.out.println(certificateDTO.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		X509Certificate cert = cg.generateCertificate(subjectData, issuerData, certificateDTO);

		try {
			if(certificateService.createCertificate(certificateDTO.getAlias(), certificateDTO.getPassword(), cert, issuerData.getPrivateKey(), ct,certificateDTO.getKeyStorePassword())){
			subject.setIsCA(true);
			subject.setHasCertificate(true);
			subjectService.save(subject);
			Certificate c = certificateService.convertFromDTO(certificateDTO);
			c.setIsRevoked(false);
			certificateService.save(c);
			
			AliasData ad= new AliasData();
			System.out.println("Iz nekog razloga ulazis ovde?????");
			ad.setId(22222L);
			ad.setAlias(certificateDTO.getAlias());
			aliasDataService.save(ad);
			}else{
				return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	/*	Certificate c = certificateService.convertFromDTO(certificateDTO);
		c.setIsRevoked(false);
		certificateService.save(c);
		
		AliasData ad= new AliasData();
		System.out.println("Iz nekog razloga ulazis ovde?????");
		ad.setId(22222L);
		ad.setAlias(certificateDTO.getAlias());
		aliasDataService.save(ad);*/
		
		return new ResponseEntity<>(certificateDTO, HttpStatus.CREATED);
		
	}
	
	//Pravljenje potpisanih sertifikata
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateDTO> addCertificate(@RequestBody CertificateDTO certificateDTO){
		if(!checkForInvalidInput(certificateDTO.getKeyStorePassword())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getPassword())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getAlias())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		if(aliasDataService.findByAlias(certificateDTO.getAlias())!=null){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		Security.addProvider(new BouncyCastleProvider());
		
		// Za sad hardkovano da su svi intermediate
		CertificateType ct = CertificateType.INTERMEDIATE;
		
		if(certificateDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		Subject subject = subjectService.findOne((Long)certificateDTO.getSubjectId());
		
		if(subject == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	/*	subject.setIsCA(true);
		subject.setHasCertificate(true);
		subjectService.save(subject);*/
		
		Subject issuer = subjectService.findOne((Long)certificateDTO.getIssuerId());
		
		KeyPair keyPair = certificateService.generateKeyPair();
		
		SubjectData subjectData = certificateService.generateSubjectData(subject);
		IssuerData issuerData = certificateService.generateIssuerData(keyPair.getPrivate(), issuer);
		
		CertificateGenerator cg = new CertificateGenerator();
		X509Certificate cert = cg.generateCertificate(subjectData, issuerData, certificateDTO);

		try {
			if(certificateService.createCertificate(certificateDTO.getAlias(), certificateDTO.getPassword(), cert, issuerData.getPrivateKey(), ct,certificateDTO.getKeyStorePassword())){
			subject.setIsCA(true);
			subject.setHasCertificate(true);
			subjectService.save(subject);
			}
			else{
				return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
			}
			
		
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
				cert = (X509Certificate) keyStoreReader.readCertificate("keystoreintermediate.p12", "123", ad2.getAlias());
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
				}else {
					aliasDataService.save(ad);
				}
			}
		}
		
		
		
		//subjectService.findOne(certificateDTO.getSubjectId()).setIsCA(iDaljeCa);
		
		//ks.deleteEntry(certificateDTO.getAlias());
		//ks.store(new FileOutputStream(new File("keystoreroot.p12")), "123".toCharArray());

		return new ResponseEntity<>(certificateDTO, HttpStatus.OK);
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

	@PostMapping(value = "/isValid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> isItAValidCertificate(@RequestBody CertificateDetailsDTO certificateDetailsDTO) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		System.out.println("test dtoAlias: "+certificateDetailsDTO.getAlias() );

		Certificate c = certificateService.findByAlias(certificateDetailsDTO.getAlias());
		Boolean valid = c.isValid();
		return new ResponseEntity<>(valid, HttpStatus.OK);
	}



	@GetMapping(value = "/get-all-root-certificates/{password}")
	public ResponseEntity<List<CertificateDetailsDTO>> getAllRootCertificates(@PathVariable (value="password")String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{

		//List<SubjectDTO> issuersDTO = new ArrayList<>();
		List<CertificateDetailsDTO> certificateDetailsDTOS = new ArrayList<>();
		FileInputStream is = new FileInputStream("keystoreroot.p12");

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(is, password.toCharArray());
		Enumeration e = keystore.aliases();
		for (; e.hasMoreElements();) {
			String alias = (String) e.nextElement();

			java.security.cert.Certificate cert = keystore.getCertificate(alias);
			if (cert instanceof X509Certificate) {
				X509Certificate x509cert = (X509Certificate) cert;

				CertificateDetailsDTO certificateDetailsDTO = new CertificateDetailsDTO(
					x509cert.getIssuerDN().getName(),
					x509cert.getSubjectDN().getName(),
					x509cert.getSerialNumber(),
					x509cert.getNotBefore(),
					x509cert.getNotAfter(),
					alias
				);
				certificateDetailsDTOS.add(certificateDetailsDTO);
			}
		}

		return new ResponseEntity<>(certificateDetailsDTOS, HttpStatus.OK);
	}
	@GetMapping(value = "/get-all-intermediate-certificates/{password}")
	public ResponseEntity<List<CertificateDetailsDTO>> getAllIntermediateCertificates(@PathVariable (value="password")String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{

		//List<SubjectDTO> issuersDTO = new ArrayList<>();
		List<CertificateDetailsDTO> certificateDetailsDTOS = new ArrayList<>();
		FileInputStream is = new FileInputStream("keystoreintermediate.p12");

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(is, password.toCharArray());
		Enumeration e = keystore.aliases();
		for (; e.hasMoreElements();) {
			String alias = (String) e.nextElement();

			java.security.cert.Certificate cert = keystore.getCertificate(alias);
			if (cert instanceof X509Certificate) {
				X509Certificate x509cert = (X509Certificate) cert;

				CertificateDetailsDTO certificateDetailsDTO = new CertificateDetailsDTO(
						x509cert.getIssuerDN().getName(),
						x509cert.getSubjectDN().getName(),
						x509cert.getSerialNumber(),
						x509cert.getNotBefore(),
						x509cert.getNotAfter(),
						alias
				);
				certificateDetailsDTOS.add(certificateDetailsDTO);
			}
		}

		return new ResponseEntity<>(certificateDetailsDTOS, HttpStatus.OK);
	}
	@PostMapping(value = "/downloadCertificate")
	public void downloadCertificate(@RequestBody CertificateDetailsDTO certificateDetails)
			throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {

		KeyStoreReader	keyStoreReader = new KeyStoreReader();

		String alias = certificateDetails.getAlias();
				
		X509Certificate certificate = (X509Certificate)keyStoreReader.readCertificate("keystoreroot.p12", "123", alias);

		StringWriter sw = new StringWriter();

		try (PemWriter pw = new PemWriter(sw)) {
		  PemObjectGenerator gen = new JcaMiscPEMGenerator(certificate);
		  pw.writeObject(gen);
		}

		FileWriter fw = new FileWriter(alias + ".cert");
		fw.write(sw.toString());
		fw.flush();
		fw.close();
		
	}
	
	public Boolean checkForInvalidInput(String text){
		Boolean isValid = true;
		for(char c:text.toCharArray()){
			int asciiC = (c);
			System.out.println(asciiC);
			if(asciiC>=0 && asciiC<=31){
				isValid = false;
				break;
			}
			else if(asciiC >= 33 && asciiC <=47){
				isValid = false;
				break;
			}
			else if(asciiC >= 58 && asciiC <=64){
				isValid = false;
				break;
			}
			else if(asciiC >= 91 && asciiC <=96){
				isValid = false;
				break;
			}else if(asciiC >= 123){
				isValid = false;
				break;
			}
		}
		
		if(text.toUpperCase().contains("DROP") || text.toUpperCase().contains("INTO")|| text.toUpperCase().contains("INSERT") || text.toUpperCase().contains("SELECT")
					|| text.toUpperCase().contains("DELETE")){
			isValid = false;
		}
		return isValid;
		
		
	}
	
	
	
}
