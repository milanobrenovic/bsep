package com.bsep.tim11.bseptim11.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(value = "https://localhost:4200")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<Subject>> getIssuersKeyStore(@PathVariable (value = "password")String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
		System.out.println("Password::: "+password);
		
		//List<SubjectDTO> issuersDTO = new ArrayList<>();
		if(!checkForInvalidInputPassword(password)){
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CertificateDTO> addRootCertificate(@RequestBody CertificateDTO certificateDTO){
		final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerCertificate.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
		if(!checkForInvalidInputPassword(certificateDTO.getKeyStorePassword())){
			logger.log(Level.SEVERE,"Incorrect keyStore password was used");

			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInputPassword(certificateDTO.getPassword())){
			logger.log(Level.SEVERE,"Given certificate password is not acceptable");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getAlias())){
			logger.log(Level.SEVERE,"Given certificate alias is not acceptable");

			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInputPassword(certificateDTO.getKeyStorePassword())){
			logger.log(Level.SEVERE,"Given keystore password is not acceptable");
		}
		if(aliasDataService.findByAlias(certificateDTO.getAlias())!=null){
			logger.log(Level.SEVERE,"Certificate with given alias already exists");

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
			logger.log(Level.SEVERE,"Subject was not passed into the function");

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
				logger.log(Level.SEVERE,"Could not create new certificate because keyStore password was incorrect");
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
		logger.log(Level.INFO, "Certificate successfully created");

		return new ResponseEntity<>(certificateDTO, HttpStatus.CREATED);
		
	}
	
	//Pravljenje potpisanih sertifikata
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CertificateDTO> addCertificate(@RequestBody CertificateDTO certificateDTO){
		final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerCertificate.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
		if(!checkForInvalidInputPassword(certificateDTO.getKeyStorePassword())){
			logger.log(Level.SEVERE,"Incorrect keyStore password was used");

			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInputPassword(certificateDTO.getPassword())){
			logger.log(Level.SEVERE,"Given certificate password is not acceptable");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(certificateDTO.getAlias())){
			logger.log(Level.SEVERE,"Given certificate alias is not acceptable");

			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		if(aliasDataService.findByAlias(certificateDTO.getAlias())!=null){
			logger.log(Level.SEVERE,"Certificate with given alias already exists");

			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		Boolean valid = false;
		List<Certificate> certs = certificateService.findAll();
		List<AliasData> certDatas = aliasDataService.findAll();
	//	for (AliasData a: certDatas){
			for(Certificate c: certs){
				if(certificateDTO.getIssuerId() == c.getSubjectId()){
					if(isCertificateValid(c)){
						valid= true;
						break;
					}
				}
			}
	//	}
		if(!valid){
			logger.log(Level.SEVERE,"Given certificate cant be created , because the referenced certificate could not be found");

			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

		}
		Security.addProvider(new BouncyCastleProvider());
		
		if(certificateDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		// U zavisnosti od toga da li je CA gledamo da li je intermediate ili end-entity
		System.out.println(certificateDTO.getIsCA());
		CertificateType ct;
		if(certificateDTO.getIsCA()) {
			System.out.println("JESTE CA USAO U IF");
			ct = CertificateType.INTERMEDIATE;
		} else {
			System.out.println("USAO U ELSE NIJE CA");
			ct = CertificateType.ENDENTITY;
		}
		
		Subject subject = subjectService.findOne((Long)certificateDTO.getSubjectId());
		
		if(subject == null) {
			logger.log(Level.SEVERE,"Selected subject could not be found");
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
				logger.log(Level.SEVERE,"KeyStore could not be accessed");

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
		Boolean nasao = false;
		List<AliasData> aliasDatas = aliasDataService.findAll();
		for (AliasData ad2 : aliasDatas){
			if(ad2.getId() == certificateDTO.getIssuerId()){
			cert = (X509Certificate) keyStoreReader.readCertificate("keystoreroot.p12", certificateDTO.getKeyStorePassword(), ad2.getAlias());
			if (cert != null) {
				nasao = true;
				System.out.println("Udjes u root?");
				
				ad.setAliasData(ad2);
				aliasDataService.save(ad);
				break;
			}
			else{
				System.out.println("Udjes u else?");
				cert = (X509Certificate) keyStoreReader.readCertificate("keystoreintermediate.p12", certificateDTO.getKeyStorePassword(), ad2.getAlias());
				if (cert != null) {
					nasao = true;

					ad.setAliasData(ad2);
					aliasDataService.save(ad);
					break;
				}
			}
			}
			if(nasao = true){
			ad2.getAliases().add(ad);
			}
		}
		
		//aliasDataService.save(ad);
		certificateService.save(c);
		logger.log(Level.INFO, "Successfully created intermediate certificate");

		return new ResponseEntity<>(certificateDTO, HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CertificateDTO> revokeCertificate(@RequestBody CertificateDTO certificateDTO) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerRevokation.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
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
		logger.log(Level.INFO, "Revoked selected certificate and all other that were dependend of it ");

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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Boolean> isItAValidCertificate(@RequestBody CertificateDetailsDTO certificateDetailsDTO) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		System.out.println("test dtoAlias: "+certificateDetailsDTO.getAlias() );

		Certificate c = certificateService.findByAlias(certificateDetailsDTO.getAlias());
		Boolean valid = c.isValid();
		return new ResponseEntity<>(valid, HttpStatus.OK);
	}



	@GetMapping(value = "/get-all-root-certificates/{password}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
					alias,
					CertificateType.ROOT,
					password
				);
				certificateDetailsDTOS.add(certificateDetailsDTO);
			}
		}

		return new ResponseEntity<>(certificateDetailsDTOS, HttpStatus.OK);
	}
	
	@GetMapping(value = "/get-all-intermediate-certificates/{password}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
						alias,
						CertificateType.INTERMEDIATE,
						password
				);
				certificateDetailsDTOS.add(certificateDetailsDTO);
			}
		}

		return new ResponseEntity<>(certificateDetailsDTOS, HttpStatus.OK);
	}
	
	@GetMapping(value = "/get-all-end-entity-certificates/{password}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<CertificateDetailsDTO>> getAllEndEntityCertificates(@PathVariable (value="password")String password ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{

		List<CertificateDetailsDTO> certificateDetailsDTOS = new ArrayList<>();
		FileInputStream is = new FileInputStream("keystoreendentity.p12");

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
						alias,
						CertificateType.ENDENTITY,
						password
				);
				certificateDetailsDTOS.add(certificateDetailsDTO);
			}
		}

		return new ResponseEntity<>(certificateDetailsDTOS, HttpStatus.OK);
	}
	
	@PostMapping(value = "/downloadCertificate")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void downloadCertificate(@RequestBody CertificateDetailsDTO certificateDetails)
			throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
		final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerDownload.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
		KeyStoreReader	keyStoreReader = new KeyStoreReader();

		String alias = certificateDetails.getAlias();

		X509Certificate certificate;
		
		if (certificateDetails.getType().equals(CertificateType.ROOT)) {
			certificate = (X509Certificate)keyStoreReader.readCertificate("keystoreroot.p12", certificateDetails.getKeyStorePassword(), alias);
		} else if(certificateDetails.getType().equals(CertificateType.INTERMEDIATE)) {
			certificate = (X509Certificate)keyStoreReader.readCertificate("keystoreintermediate.p12", certificateDetails.getKeyStorePassword(), alias);
		} else {
			certificate = (X509Certificate)keyStoreReader.readCertificate("keystoreendentity.p12", certificateDetails.getKeyStorePassword(), alias);
		}
		
		StringWriter sw = new StringWriter();

		try (PemWriter pw = new PemWriter(sw)) {
		  PemObjectGenerator gen = new JcaMiscPEMGenerator(certificate);
		  pw.writeObject(gen);
		}
		
		FileWriter fw = new FileWriter(alias + ".cer");
		fw.write(sw.toString());
		fw.flush();
		fw.close();
		logger.log(Level.INFO, "Certificate has been downloaded");
		
	}
	
	public Boolean checkForInvalidInput(String text){
		Boolean isValid = true;
		for(char c:text.toCharArray()){
			int asciiC = (c);
		//System.out.println(asciiC);
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
	public Boolean isCertificateValid(Certificate c){
		if(aliasDataService.findOne(c.getId()).getAliasData()!=null){
			if(!isCertificateValid(certificateService.findOne(aliasDataService.findOne(c.getId()).getAliasData().getId()))){
				return false;
			}
		}
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date now = new Date();  
		   System.out.println(df.format(now));
		   if((now).compareTo(c.getStartDate()) < 0){
			   return false;
		   }
		   else if(now.compareTo(c.getEndDate()) > 0){
			   return false;
		   }
		   return true;
	}
	public Boolean checkForInvalidInputPassword(String text){
		Boolean isValid = true;
		for(char c:text.toCharArray()){
			int asciiC = (c);
			//System.out.println(asciiC);
			if(asciiC>=0 && asciiC<=31){
				isValid = false;
				break;
			}
		/*	else if(asciiC >= 33 && asciiC <=45){
				isValid = false;
				break;
			}*/
		/*	else if(asciiC == 47){
				isValid = false;
				break;
			}*/
			/*else if(asciiC >= 58 && asciiC <=63){
				isValid = false;
				break;
			}*/
		/*	else if(asciiC >= 91 && asciiC <=96){
				isValid = false;
				break
			}*/else if(asciiC == 127){
				isValid = false;
				break;
			}
		}
		
		if(text.toUpperCase().contains("DROP") ||
			text.toUpperCase().contains("UPDATE") ||
			text.toUpperCase().contains("INTO") ||
			text.toUpperCase().contains("DELETE") ||
			text.toUpperCase().contains("INSERT") ||
			text.toUpperCase().contains("SELECT")){
			isValid = false;
		}
		return isValid;
		
		
	}
	
	
}
