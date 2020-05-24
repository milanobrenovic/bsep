package com.bsep.tim11.bseptim11.service;

import com.bsep.tim11.bseptim11.model.Authority;
import com.bsep.tim11.bseptim11.model.UserTokenState;
import com.bsep.tim11.bseptim11.security.auth.JwtAuthenticationRequest;

import java.util.Set;

public interface AuthService {

    Set<Authority> findById(Long id);
    Set<Authority> findByName(String name);
    UserTokenState login(JwtAuthenticationRequest jwtAuthenticationRequest);

}
