package com.bsep.tim11.bseptim11.repository;

import com.bsep.tim11.bseptim11.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);

}
