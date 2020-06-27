package com.bsep.tim11.bseptim11.repository;

import com.bsep.tim11.bseptim11.model.NormalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalUserRepository extends JpaRepository<NormalUser, Long> {

    NormalUser findOneById(Long id);
    NormalUser findByUsername(String username);

}
