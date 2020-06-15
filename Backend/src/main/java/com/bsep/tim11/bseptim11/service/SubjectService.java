package com.bsep.tim11.bseptim11.service;

import java.util.List;

import com.bsep.tim11.bseptim11.dto.SubjectDTO;
import com.bsep.tim11.bseptim11.model.Subject;
import com.bsep.tim11.bseptim11.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;
	
	public Subject findOne(Long id) {
		return subjectRepository.findOneById(id);
	}
	public Subject findByEmail(String email){
		return subjectRepository.findOneByEmail(email);
	}
	
	public List<Subject> findAll(){
		return subjectRepository.findAll();
	}
	
	public Subject save(Subject subject) {
		return subjectRepository.save(subject);
	}
	
	public Subject convertFromDTO(SubjectDTO subjectDTO) {
		Subject subject = new Subject();
		
		subject.setCommonName(subjectDTO.getCommonName());
		subject.setSurname(subjectDTO.getSurname());
		subject.setGivenName(subjectDTO.getGivenName());
		subject.setOrganizationUnit(subjectDTO.getOrganizationUnit());
		subject.setOrganization(subjectDTO.getOrganization());
		subject.setCountry(subjectDTO.getCountry());
		subject.setEmail(subjectDTO.getEmail());
		subject.setIsCA(subjectDTO.getIsCA());
		subject.setHasCertificate(subjectDTO.getHasCertificate());
		
		return subject;
	}
	
}
