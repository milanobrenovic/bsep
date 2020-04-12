package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.EntityDTO;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

public interface EntityService {

    EntityDTO createSubject(EntityDTO entityDTO) throws IllegalArgumentException, BadCredentialsException;
    List<EntityDTO> findAll();
    List<EntityDTO> getAllWithoutRootEntities();

}
