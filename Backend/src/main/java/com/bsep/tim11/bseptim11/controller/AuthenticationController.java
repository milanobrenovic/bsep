package com.bsep.tim11.bseptim11.controller;

import com.bsep.tim11.bseptim11.model.UserTokenState;
import com.bsep.tim11.bseptim11.security.auth.JwtAuthenticationRequest;
import com.bsep.tim11.bseptim11.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = { "https://localhost:4200"})
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            UserTokenState userTokenState = authService.login(authenticationRequest);

            if (userTokenState == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(userTokenState, HttpStatus.OK);
            }
        } catch (AuthenticationException e) {
//            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
