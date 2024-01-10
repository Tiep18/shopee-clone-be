package com.pt.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResDTO {
    @JsonProperty("access_token")
    private String accessToken;
}
