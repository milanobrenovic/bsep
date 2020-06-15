package com.bsep.tim11.bseptim11.repository;

import java.util.List;

import com.bsep.tim11.bseptim11.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
	
	Subject findOneById(Long Id);
	Subject findOneByEmail(String Email);
	List<Subject> findAll();
	
}
