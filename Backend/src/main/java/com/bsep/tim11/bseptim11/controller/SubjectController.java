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
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:4200", "http://localhost:8080" })
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
		
		if (subjectDTO.getClass() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		Subject subject = subjectService.convertFromDTO(subjectDTO);
		
		subject = subjectService.save(subject);
		
		return new ResponseEntity<>(subjectDTO, HttpStatus.CREATED);
		
	}
	
}
