package com.ezekielwong.third.party.app.domain.request.ms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest implements Serializable {

    @JsonProperty("grant_type")
    private String grantType;

    private String assertion;
}
