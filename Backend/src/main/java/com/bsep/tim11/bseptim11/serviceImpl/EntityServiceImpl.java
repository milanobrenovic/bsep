package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.EntityDTO;
import com.bsep.tim11.bseptim11.enumeration.EntityType;
import com.bsep.tim11.bseptim11.exceptions.InvalidEntityDataException;
import com.bsep.tim11.bseptim11.model.Entity;
import com.bsep.tim11.bseptim11.repository.EntityRepository;
import com.bsep.tim11.bseptim11.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityServiceImpl implements EntityService {

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public EntityDTO createSubject(EntityDTO entityDTO) throws InvalidEntityDataException {
        isSubjectValid(entityDTO);

        EntityType entityType = EntityType.valueOf(entityDTO.getType().toUpperCase());
        Entity subject = new Entity(entityType, entityDTO.getCommonName(), entityDTO.getEmail(),
                entityDTO.getOrganizationUnit(), entityDTO.getOrganization(), entityDTO.getCountryCode(),
                entityDTO.getSurname(), entityDTO.getGivename());

        return new EntityDTO(entityRepository.save(subject));
    }

    @Override
    public List<EntityDTO> findAll() {
        return convertToDTO(entityRepository.findAll());
    }

    @Override
    public List<EntityDTO> getAllWithoutRootEntities() {
        return convertToDTO(entityRepository.findByNumberOfRootCertificatesEquals(0));
    }

    private List<EntityDTO> convertToDTO(List<Entity> entities) {
        List<EntityDTO> entityDTOS = new ArrayList<>();
        for (Entity entity : entities) {
            entityDTOS.add(new EntityDTO(entity));
        }
        return entityDTOS;
    }

    private boolean isSubjectValid(EntityDTO entityDTO) throws InvalidEntityDataException {
        EntityType entityType = EntityType.valueOf(entityDTO.getType().toUpperCase());

        if (entityRepository.findByCommonName(entityDTO.getCommonName()) != null) {
            throw new InvalidEntityDataException("Subject with the same common name already exists.");
        }

        if (entityType.toString().equals("USER")) {
            if (entityDTO.getEmail().isEmpty() || entityDTO.getSurname().isEmpty() || entityDTO.getGivename().isEmpty()) {
                throw new InvalidEntityDataException("Email, surname or givename is missing.");
            }
            if (entityRepository.findByEmail(entityDTO.getEmail()) != null) {
                throw  new InvalidEntityDataException("Subject with the same email address already exists.");
            }
        } else {
            if (entityDTO.getOrganizationUnit().isEmpty()) {
                throw new InvalidEntityDataException("Organization unit is missing.");
            }
        }

        return true;
    }

}
