package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.model.Admin;
import com.bsep.tim11.bseptim11.model.Authority;
import com.bsep.tim11.bseptim11.model.UserTokenState;
import com.bsep.tim11.bseptim11.repository.AuthRepository;
import com.bsep.tim11.bseptim11.security.TokenUtils;
import com.bsep.tim11.bseptim11.security.auth.JwtAuthenticationRequest;
import com.bsep.tim11.bseptim11.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private AuthRepository authorityRepository;

    @Override
    public Set<Authority> findById(Long id) {
        Authority authority = this.authorityRepository.getOne(id);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        return authorities;
    }

    @Override
    public Set<Authority> findByName(String name) {
        Authority authority = this.authorityRepository.findByName(name);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        return authorities;
    }

    @Override
    public UserTokenState login(JwtAuthenticationRequest jwtAuthenticationRequest) {
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                jwtAuthenticationRequest.getUsername(),
                jwtAuthenticationRequest.getPassword()
        );
        final Authentication authentication = authManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Admin admin = (Admin) authentication.getPrincipal();
        String jwtAccessToken = tokenUtils.generateToken(admin.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        return new UserTokenState(
                jwtAccessToken,
                expiresIn
        );
    }

}
