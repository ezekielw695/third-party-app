package com.ezekielwong.third.party.app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ezekielwong.third.party.app.domain.response.AccessTokenResponse;
import com.ezekielwong.third.party.app.domain.response.AccessTokenErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Component
public class JwtUtils {

    // Use BouncyCastle cryptographic provider instead of JVM default as BouncyCastle is able to load PCKS#1 keys
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Integration key of the application
     */
    @Value("${jwt.iss}")
    private String iss;

    /**
     * ms-docs public key
     */
    @Value("${jwt.public-key}")
    private String publicKeyPath;

    /**
     * ms-docs RSA private key
     */
    @Value("${jwt.rsa-private-key}")
    private String rsaPrivateKeyPath;

    public Object decodeJWT(String jwt) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        log.debug("Decoding JSON web token");
        log.debug(jwt);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        String publicKey = Files.readString(Path.of(publicKeyPath));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey)));

        String privateKey = Files.readString(Path.of(rsaPrivateKeyPath));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        JWTVerifier verifier = JWT.require(Algorithm.RSA256(pubKey, privKey))
                .withIssuer(iss).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(jwt);

            return AccessTokenResponse.builder()
                    .accessToken(decodedJWT.getToken())
                    .tokenType("Bearer")
                    .expiresIn(3600)
                    .build();

        // Invalid signature/claims
        } catch (JWTVerificationException exception) {

            Throwable cause = exception.getCause();
            String errMsg = (cause != null ? cause.getMessage() : exception.getMessage());
            log.error(errMsg);

            return AccessTokenErrorResponse.builder()
                    .error("Invalid signature/claims")
                    .errorDesc(errMsg)
                    .build();
        }
    }
}
