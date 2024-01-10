package com.pt.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO {
    String message;
    Object data;

    public ResponseDTO(String message) {
        this.message = message;
    }
}
