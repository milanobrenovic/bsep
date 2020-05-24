package com.bsep.tim11.bseptim11.service;

import java.util.List;

import com.bsep.tim11.bseptim11.model.AliasData;
import com.bsep.tim11.bseptim11.repository.AliasDataRepository;
import org.hibernate.sql.Alias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliasDataService {

	@Autowired
	private AliasDataRepository aliasDataRepository;
	
	public AliasDataService(){}
	
	public AliasData findOne(Long id){
		return aliasDataRepository.findOneById(id);
	}
	public AliasData save(AliasData aliasData) {
		return aliasDataRepository.save(aliasData);
	}
	public List<AliasData> findAll(){
		return aliasDataRepository.findAll();
	}
	public void remove(Long id){
		aliasDataRepository.deleteById(id);
	}
	
}
