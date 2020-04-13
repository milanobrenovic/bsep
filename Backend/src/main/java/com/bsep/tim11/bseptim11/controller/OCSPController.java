package com.bsep.tim11.bseptim11.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.bsep.tim11.bseptim11.dto.ResponseCertificateDTO;
import com.bsep.tim11.bseptim11.dto.RevokeCertificateDTO;
import com.bsep.tim11.bseptim11.enumeration.CertificateStatus;
import com.bsep.tim11.bseptim11.service.OCSPService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/ocsp")
public class OCSPController {

    @Autowired
    private OCSPService ocspService;

    @PostMapping(
        value = "/check-status",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<CertificateStatus> checkStatus(@RequestBody String jsonString) throws
            JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        JsonNode certNode = jsonNode.get("OCSP Request Data").get("Requestor List").get("Certificate ID");
        String serialNumber = certNode.get("Serial Number").asText();

        return new ResponseEntity<>(ocspService.checkStatus(serialNumber), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CertificateStatus> getCertificateStatus(
        @RequestParam(value = "sn", required = true) String serialNumber
    ) {
        return new ResponseEntity<>(ocspService.checkStatus(serialNumber), HttpStatus.OK);
    }

    @PutMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize(
        "hasRole('ADMIN')"
    )
    public ResponseEntity<ResponseCertificateDTO> revoke(
        @Valid @RequestBody RevokeCertificateDTO revokeCertificateDTO
    ) throws Exception {
        return new ResponseEntity<>(ocspService.revoke(revokeCertificateDTO), HttpStatus.OK);
    }

}
