package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.LoggedInUserDTO;
import com.bsep.tim11.bseptim11.model.Admin;
import com.bsep.tim11.bseptim11.model.Authority;
import com.bsep.tim11.bseptim11.dto.UserTokenStateDTO;
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
    public LoggedInUserDTO login(JwtAuthenticationRequest jwtAuthenticationRequest) {
        final Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                jwtAuthenticationRequest.getUsername(),
                jwtAuthenticationRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = returnUsername(authentication.getPrincipal());
        if (username == null) {
            return null;
        }

        String jwtToken = tokenUtils.generateToken(username);
        int expiresIn = tokenUtils.getExpiredIn();

        return returnLoggedInUser(
                authentication.getPrincipal(),
                new UserTokenStateDTO(jwtToken, expiresIn)
        );
    }

    private String returnUsername(Object object) {
        if (object instanceof Admin) {
            return ((Admin) object).getUsername();
        }
        return null;
    }

    private LoggedInUserDTO returnLoggedInUser(Object object, UserTokenStateDTO userTokenStateDTO) {
        if (object instanceof Admin) {
            Admin admin = (Admin) object;
            return new LoggedInUserDTO(
                admin.getId(),
                admin.getUsername(),
                "ROLE_ADMIN",
                    userTokenStateDTO
            );
        }
        return null;
    }

}
