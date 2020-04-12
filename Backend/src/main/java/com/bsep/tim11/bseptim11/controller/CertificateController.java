package com.bsep.tim11.bseptim11.controller;

import com.bsep.tim11.bseptim11.dto.CertificateDTO;
import com.bsep.tim11.bseptim11.dto.NewCertificateDTO;
import com.bsep.tim11.bseptim11.dto.ExistingCertificateDTO;
import com.bsep.tim11.bseptim11.dto.DownloadCertificateDTO;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private KeyStoreService keyStoreService;

    @PostMapping(
        value = "/self-signed",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<ExistingCertificateDTO> createSelfSigned(
        @Valid @RequestBody NewCertificateDTO newCertificateDTO
    ) {
        System.out.println(newCertificateDTO.toString());
        try {
            ExistingCertificateDTO existingCertificateDTO = certificateService.createSelfSigned(newCertificateDTO);
            if (existingCertificateDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(existingCertificateDTO, HttpStatus.OK);
        } catch (Exception e) {
//            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(
        value = "/new",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<ExistingCertificateDTO> create(
        @Valid @RequestBody NewCertificateDTO newCertificateDTO
    ) {
        try {
            ExistingCertificateDTO existingCertificateDTOTemp = certificateService.create(newCertificateDTO);
            if (existingCertificateDTOTemp == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(existingCertificateDTOTemp, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
        value = "/all"
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<List<CertificateDTO>> getAllCertificates(
        @RequestParam(value = "role", required = true) String role,
        @RequestParam(value = "keyStorePassword", required = true) String keyStorePassword
    ) {
        try {
            return new ResponseEntity<>(keyStoreService.getCertificates(role, keyStorePassword), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<List<CertificateDTO>> getCACertificates(
        @RequestParam(value = "rootKeyStoragePassword") String rootKeyStoragePassword,
        @RequestParam(value = "intermediateKeyStoragePassword") String intermediateKeyStoragePassword
    ) {
        try {
            return new ResponseEntity<>(keyStoreService.getCACertificates(rootKeyStoragePassword, intermediateKeyStoragePassword), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(
        value = "/download",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<Void> download(@Valid @RequestBody DownloadCertificateDTO downloadCertificateDTO) {
        try {
            certificateService.download(downloadCertificateDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CertificateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (KeyStoreException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
