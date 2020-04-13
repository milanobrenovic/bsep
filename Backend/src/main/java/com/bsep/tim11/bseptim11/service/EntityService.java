package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.EntityDTO;
import com.bsep.tim11.bseptim11.exceptions.InvalidEntityDataException;

import java.util.List;

public interface EntityService {

    EntityDTO createSubject(EntityDTO entityDTO) throws InvalidEntityDataException;
    List<EntityDTO> findAll();
    List<EntityDTO> getAllWithoutRootEntities();

}
