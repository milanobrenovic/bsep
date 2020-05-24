package com.bsep.tim11.bseptim11.repository;

import com.bsep.tim11.bseptim11.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);

}
