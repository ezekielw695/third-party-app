package com.ezekielwong.third.party.app.advice;

import com.ezekielwong.third.party.app.controller.ThirdPartyAppController;
import com.ezekielwong.third.party.app.domain.response.AccessTokenErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ThirdPartyAppController.class)
public class ThirdPartyAppControllerAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            NoSuchAlgorithmException.class,
            IOException.class,
            InvalidKeyException.class
    })
    public AccessTokenErrorResponse handleCheckedException(Exception exception) {

        Throwable cause = exception.getCause();
        return new AccessTokenErrorResponse((cause != null ? cause.getMessage() : exception.getMessage()), exception.getMessage());
    }
}
