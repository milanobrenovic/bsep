package com.bsep.tim11.bseptim11.controller;

import com.bsep.tim11.bseptim11.dto.LoggedInUserDTO;
import com.bsep.tim11.bseptim11.dto.UserTokenStateDTO;
import com.bsep.tim11.bseptim11.security.auth.JwtAuthenticationRequest;
import com.bsep.tim11.bseptim11.service.AuthService;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    	final Logger logger = Logger.getLogger("");
	    FileHandler fh = null;
		try {
			fh=new FileHandler("loggerAuthentication.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
    	fh.setFormatter(new SimpleFormatter());
    	l.addHandler(fh);
		l.setLevel(Level.CONFIG);
        try {
            LoggedInUserDTO loggedInUserDTO = authService.login(authenticationRequest);

            if (loggedInUserDTO == null) {
    			logger.log(Level.SEVERE, "Failed login attempt");

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
        		logger.log(Level.INFO, "User logged with username: "+authenticationRequest.getUsername());

                return new ResponseEntity<>(loggedInUserDTO, HttpStatus.OK);
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        logger.log(Level.SEVERE, "Failed login attempt");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
