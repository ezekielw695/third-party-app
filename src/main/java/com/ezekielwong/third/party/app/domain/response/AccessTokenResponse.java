package com.ezekielwong.third.party.app.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Third party app access token API response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
}
