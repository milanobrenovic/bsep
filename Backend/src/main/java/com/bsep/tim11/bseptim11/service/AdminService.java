package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.model.Admin;

public interface AdminService {

    Admin findByUsername(String username);

}
