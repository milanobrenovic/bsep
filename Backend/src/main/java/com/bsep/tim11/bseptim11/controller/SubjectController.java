package com.bsep.tim11.bseptim11.controller;

import java.util.ArrayList;
import java.util.List;

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
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<SubjectDTO>> getAllSubjects(){
		
		List<Subject> subjects = subjectService.findAll();
		
		List<SubjectDTO> subjectsDTO = new ArrayList<>();
		for (Subject subject : subjects) {
			subjectsDTO.add(new SubjectDTO(subject));
		}
		
		return new ResponseEntity<>(subjectsDTO, HttpStatus.OK);
	}
	
	@GetMapping(value ="/{id}")
	//@PreAuthorize("hasRole('ADMIN')")
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
	public ResponseEntity<SubjectDTO> addSubject(@RequestBody SubjectDTO subjectDTO){
		
		System.out.println("subject: "+subjectDTO.toString());
		
		
		if (subjectDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		if(!checkForInvalidInput(subjectDTO.getCommonName())){
			System.out.println(">>> getCommonName");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getCountry())){
			System.out.println(">>> getCountry");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getGivenName())){
			System.out.println(">>> getGivenName");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getOrganization())){
			System.out.println(">>> getOrganization");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getOrganizationUnit())){
			System.out.println(">>> getOrganizationUnit");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInput(subjectDTO.getSurname())){
			System.out.println(">>> getSurname");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		else if(!checkForInvalidInputEmail(subjectDTO.getEmail())){
			System.out.println(">>> getEmail");
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
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(subjectDTO, HttpStatus.CREATED);
		
	}

	//Svi ponudjeni subjecti za sertifikat
	@GetMapping(value = "/subjects-without-certificate")
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
		
		if(text.toUpperCase().contains("DROP") || text.toUpperCase().contains("INTO") || text.toUpperCase().contains("DELETE") || text.toUpperCase().contains("INSERT") || text.toUpperCase().contains("SELECT")){
			isValid = false;
		}
		return isValid;
		
		
	}
	
	
}
