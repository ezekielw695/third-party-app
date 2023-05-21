package com.ezekielwong.third.party.app.controller;

import com.ezekielwong.third.party.app.domain.request.ms.WorkflowRequest;
import com.ezekielwong.third.party.app.domain.request.ms.AccessTokenRequest;
import com.ezekielwong.third.party.app.domain.response.WorkflowResponse;
import com.ezekielwong.third.party.app.domain.response.AccessTokenErrorResponse;
import com.ezekielwong.third.party.app.domain.response.ThirdPartyAppErrorResponse;
import com.ezekielwong.third.party.app.service.ThirdPartyAppServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ThirdPartyAppController {

    private final Boolean hasError = false;

    private final ThirdPartyAppServiceImpl thirdPartyAppServiceImpl;

    @PostMapping(value = "/oauth/token")
    public ResponseEntity<Object> authenticateJwt(@RequestBody MultiValueMap<String, String> tokenRequest, HttpServletRequest servletRequest)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        printRequestHeaders(servletRequest);

        log.debug("Mapping token request");
        AccessTokenRequest accessTokenRequest = mapTokenRequest(tokenRequest);
        log.info("Token request mapped");

        // Invalid grant_type
        if(!StringUtils.equals(accessTokenRequest.getGrantType(), "urn:ietf:params:oauth:grant-type:jwt-bearer")) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AccessTokenErrorResponse.builder()
                            .error("Invalid grant_type")
                            .errorDesc("Invalid grant_type")
                            .build());
        }

        log.debug("Verifying JSON web token");
        Object tokenResponse = thirdPartyAppServiceImpl.verifyJWT(accessTokenRequest.getAssertion());
        log.info("JSON web token decoded");

        log.debug(tokenResponse.toString());

        if (tokenResponse.getClass() == AccessTokenErrorResponse.class) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenResponse);
        } else {
            return ResponseEntity.ok().body(tokenResponse);
        }
    }

    @PostMapping(value = "/3b719479-f5ca-476d-b76a-dbf2f6143b5d/workflows")
    public ResponseEntity<Object> receiveWorkflow(@RequestBody WorkflowRequest workflowRequest, HttpServletRequest servletRequest) {

        printRequestHeaders(servletRequest);
        log.debug(workflowRequest.toString());

        if (hasError.equals(Boolean.TRUE)) {

            ThirdPartyAppErrorResponse.Error error = ThirdPartyAppErrorResponse.Error.builder()
                    .httpStatusCode(422)
                    .errorCode(101)
                    .errorMessage("Validation Error")
                    .referenceId("123")
                    .build();

            ThirdPartyAppErrorResponse.ValidationError validationError = ThirdPartyAppErrorResponse.ValidationError.builder()
                    .errorCode(1001)
                    .propertyName("Name")
                    .errorMessage("Names cannot contain the following characters: \\ / : * ? \" &lt; > |")
                    .build();

            ThirdPartyAppErrorResponse thirdPartyAppErrorResponse = ThirdPartyAppErrorResponse.builder()
                    .error(error)
                    .validationErrorList(Collections.singletonList(validationError))
                    .build();

                    log.debug(thirdPartyAppErrorResponse.toString());

            return ResponseEntity.badRequest().body(thirdPartyAppErrorResponse);

        } else {

            WorkflowResponse response = WorkflowResponse.builder()
                    .name("Initiate Agreement Workflow")
                    .startDate(LocalDateTime.now().toString())
                    .status("Executing")
                    .info("")
                    .build();

            log.debug(response.toString());

            return ResponseEntity.ok(response);
        }
    }

    private void printRequestHeaders(HttpServletRequest servletRequest) {

        log.debug("----- Retrieving headers -----");
        Enumeration<String> headerNames = servletRequest.getHeaderNames();

        if (headerNames != null) {

            while (headerNames.hasMoreElements()) {
                log.debug(servletRequest.getHeader(headerNames.nextElement()));
            }
        }

        log.debug("----- Headers retrieved -----");
    }

    private AccessTokenRequest mapTokenRequest(MultiValueMap<String, String> tokenRequest) {

        HashMap<String, String> tokenRequestMap = new HashMap<>();
        Arrays.asList(new String[]{"grant_type", "assertion"})
                .forEach(key -> tokenRequestMap.put(key, (tokenRequest.get(key).get(0))));

        AccessTokenRequest accessTokenRequest = new ObjectMapper().convertValue(tokenRequestMap, AccessTokenRequest.class);
        log.debug(accessTokenRequest.toString());

        return accessTokenRequest;
    }
}
