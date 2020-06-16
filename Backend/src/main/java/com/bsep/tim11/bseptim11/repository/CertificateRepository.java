package com.bsep.tim11.bseptim11.repository;

import java.util.List;

import com.bsep.tim11.bseptim11.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long>{

	Certificate findOneById(Long id);
	Certificate findOneByAlias(String alias);
	
	List<Certificate> findAll();
	
}
