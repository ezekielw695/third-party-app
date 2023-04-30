package com.ezekielwong.third.party.app.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface ThirdPartyAppService {

    Object verifyJWT(String jwt) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException;
}
