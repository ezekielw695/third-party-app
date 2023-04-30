package com.ezekielwong.third.party.app.service;

import com.ezekielwong.third.party.app.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyAppServiceImpl implements ThirdPartyAppService {

    private final JwtUtils jwtUtils;

    @Override
    public Object verifyJWT(String jwt) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        return jwtUtils.decodeJWT(jwt);
    }
}
