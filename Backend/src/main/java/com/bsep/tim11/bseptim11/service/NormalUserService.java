package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.dto.NormalUserDTO;
import com.bsep.tim11.bseptim11.model.NormalUser;

public interface NormalUserService {

    NormalUser findById(Long id);
    NormalUser findOneByUsername(String username);
    NormalUser getUserLogin();
    NormalUserDTO createNormalUser(NormalUserDTO normalUser);

}
