package com.bsep.tim11.bseptim11.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bsep.tim11.bseptim11.dto.SubjectDTO;
import com.bsep.tim11.bseptim11.model.Subject;
import com.bsep.tim11.bseptim11.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { "https://localhost:4200"})
@RequestMapping(value = "/api/subject")
public class SubjectController {

	@Autowired
	private SubjectService subjectService;
	  
	@GetMapping(value = "/all")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<SubjectDTO>> getAllSubjects(){
		
		List<Subject> subjects = subjectService.findAll();
		
		List<SubjectDTO> subjectsDTO = new ArrayList<>();
		for (Subject subject : subjects) {
			subjectsDTO.add(new SubjectDTO(subject));
		}
		
		return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
	}
	
	@GetMapping(value ="/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<SubjectDTO> getSubject(@PathVariable Long id){
		
		Subject subject = subjectService.findOne(id);
		
		if (subject == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new SubjectDTO(subject), HttpStatus.OK);
	}
	
	@PostMapping(
			value = "/create-subject",
			consumes = MediaType.APPLICATION_JSON_VALUE
	)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<SubjectDTO> addSubject(@RequestBody SubjectDTO subjectDTO){
		final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerExample.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
		
		//System.out.println("subject: "+subjectDTO.toString());
		
	//	logger.log(Level.SEVERE, "message 2");
	//	logger.log(Level.FINE, "message 3");
		
		if (subjectDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		if(!checkForInvalidInput(subjectDTO.getCommonName())){
			System.out.println(">>> getCommonName");
			logger.log(Level.SEVERE, subjectDTO.getCommonName()+" for a commonName is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getCountry())){
			System.out.println(">>> getCountry");
			logger.log(Level.SEVERE, subjectDTO.getCountry()+" for a country is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getGivenName())){
			System.out.println(">>> getGivenName");
			logger.log(Level.SEVERE, subjectDTO.getGivenName()+" for a given name is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getOrganization())){
			System.out.println(">>> getOrganization");
			logger.log(Level.SEVERE, subjectDTO.getOrganization()+" for an organization is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getOrganizationUnit())){
			System.out.println(">>> getOrganizationUnit");
			logger.log(Level.SEVERE, subjectDTO.getOrganizationUnit()+" for an organization unit is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getSurname())){
			System.out.println(">>> getSurname");
			logger.log(Level.SEVERE, subjectDTO.getSurname()+" for a surname is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInputEmail(subjectDTO.getEmail())){
			System.out.println(">>> getEmail");
			logger.log(Level.SEVERE, subjectDTO.getEmail()+" for an email is not allowed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		
		Subject subject = subjectService.convertFromDTO(subjectDTO);
		Boolean emailExists = false;
		List<Subject> subjects = subjectService.findAll();
		if(subjectService.findByEmail(subject.getEmail()) != null){
			emailExists = true;
		}
		
		if (!emailExists) {
			subjectService.save(subject);
		}
		else{
			logger.log(Level.SEVERE, subjectDTO.getEmail()+" already in use");
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		logger.log(Level.INFO, "Subject was created with unique email: "+subjectDTO.getEmail());
		return new ResponseEntity<>(subjectDTO, HttpStatus.CREATED);
		
	}

	//Svi ponudjeni subjecti za sertifikat
	@GetMapping(value = "/subjects-without-certificate")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<SubjectDTO>> getSubjectsWithoutCertificate(){

		List<SubjectDTO> subjectsDTO = new ArrayList<>();
		List<Subject> subjects = subjectService.findAll();

		for (Subject subject : subjects) {
			if(!subject.getHasCertificate())
				subjectsDTO.add(new SubjectDTO(subject));
		}

		return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);

	}
	
	
	
	public Boolean checkForInvalidInput(String text){
		Boolean isValid = true;
		for(char c:text.toCharArray()){
			int asciiC = (c);
		//	System.out.println(asciiC);
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
		
		if(text.toUpperCase().contains("DROP") || text.toUpperCase().contains("DELETE") || text.toUpperCase().contains("INSERT") || text.toUpperCase().contains("SELECT")){
			isValid = false;
		}
		return isValid;
		
		
	}
	public Boolean checkForInvalidInputEmail(String text){
		Boolean isValid = true;
		for(char c:text.toCharArray()){
			int asciiC = (c);
			//System.out.println(asciiC);
			if(asciiC>=0 && asciiC<=31){
				isValid = false;
				break;
			}
			else if(asciiC >= 33 && asciiC <=45){
				isValid = false;
				break;
			}
			else if(asciiC == 47){
				isValid = false;
				break;
			}
			else if(asciiC >= 58 && asciiC <=63){
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
