package com.bsep.tim11.bseptim11.repository;

import java.util.List;

import com.bsep.tim11.bseptim11.model.AliasData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AliasDataRepository extends JpaRepository<AliasData, Long>{
	AliasData findOneById(Long id);
	List<AliasData> findAll();
	AliasData findOneByAlias(String alias);
}
