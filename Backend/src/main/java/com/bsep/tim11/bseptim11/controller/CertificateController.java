package com.bsep.tim11.bseptim11.controller;

import com.bsep.tim11.bseptim11.dto.*;
import com.bsep.tim11.bseptim11.service.CertificateService;
import com.bsep.tim11.bseptim11.service.KeyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<ResponseCertificateDTO> createSelfSigned(
        @Valid @RequestBody NewCertificateDTO newCertificateDTO
    ) throws Exception {

        return new ResponseEntity<>(certificateService.createSelfSigned(newCertificateDTO), HttpStatus.CREATED);
    }

    @PostMapping(
        value = "/new",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<ResponseCertificateDTO> create(
        @Valid @RequestBody NewCertificateDTO newCertificateDTO
    ) throws Exception {

        return new ResponseEntity<>(certificateService.create(newCertificateDTO), HttpStatus.CREATED);
    }

    @GetMapping(
        value = "/all"
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<List<CertificateItemDTO>> getAllCertificates(
        @RequestParam(value = "role", required = true) String role,
        @RequestParam(value = "keyStorePassword", required = true) String keyStorePassword
    ) throws Exception {

        return new ResponseEntity<>(keyStoreService.getCertificates(role, keyStorePassword), HttpStatus.OK);
    }

    @GetMapping(
        value = "/{subjectId}"
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<List<CertificateDTO>> getCACertificates(
        @PathVariable Long subjectId,
        @RequestParam(value = "rootKeyStoragePassword", required = false) String rootKeyStoragePassword,
        @RequestParam(value = "intermediateKeyStoragePassword", required = false) String intermediateKeyStoragePassword
    ) throws Exception {

        return new ResponseEntity<>(keyStoreService.getCACertificates(
            subjectId,
            rootKeyStoragePassword,
            intermediateKeyStoragePassword
        ), HttpStatus.OK);
    }

    @PostMapping(
        value = "/download",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<Void> download(@Valid @RequestBody DownloadCertificateDTO downloadCertificateDTO) throws
            Exception {

        certificateService.download(downloadCertificateDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
