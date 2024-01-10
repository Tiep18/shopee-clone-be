package com.pt.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshTokenReqDTO {
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
