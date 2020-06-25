package com.bsep.tim11.bseptim11.controller;

import com.bsep.tim11.bseptim11.dto.LoggedInUserDTO;
import com.bsep.tim11.bseptim11.security.auth.JwtAuthenticationRequest;
import com.bsep.tim11.bseptim11.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(value = "https://localhost:4200")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoggedInUserDTO> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            LoggedInUserDTO loggedInUserDTO = authService.login(authenticationRequest);

            if (loggedInUserDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(loggedInUserDTO, HttpStatus.OK);
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
