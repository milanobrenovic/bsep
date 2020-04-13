package com.bsep.tim11.bseptim11.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.OperatorCreationException;
import org.ietf.jgss.GSSException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.text.ParseException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestfulExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidCertificateDataException.class)
    protected ResponseEntity<Object> handleInvalidCertificateDataException(InvalidCertificateDataException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage(e.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(UnrecoverableKeyException.class)
    protected ResponseEntity<Object> handleInvalidEntityDataException(InvalidEntityDataException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage(e.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(InvalidEntityDataException.class)
    protected ResponseEntity<Object> handleUnrecoverableKeyException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage("Invalid password for the private key.");
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage("Invalid password for the key store.");
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler({
        CertificateEncodingException.class, KeyStoreException.class, CertificateException.class,
        NoSuchAlgorithmException.class, CertificateEncodingException.class, NoSuchProviderException.class,
        OperatorCreationException.class, CertIOException.class, CertificateParsingException.class,
        OCSPException.class, InvalidAlgorithmParameterException.class, GSSException.class
    })
    protected ResponseEntity<Object> handleCertificateAndKeyStoreException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        exceptionResponse.setMessage("An exception occurred while handling certificate and key store.");
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(ParseException.class)
    protected ResponseEntity<Object> handleParseException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage("Invalid date format.");
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<Object> handleJsonProcessingException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        exceptionResponse.setMessage("Certificate status check failed.");
        return buildResponseEntity(exceptionResponse);
    }

    private ResponseEntity<Object> buildResponseEntity(ExceptionResponse exceptionResponse) {
        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getStatus());
    }

}
