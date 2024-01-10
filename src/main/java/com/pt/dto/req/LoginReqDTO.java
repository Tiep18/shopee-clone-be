package com.pt.dto.req;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginReqDTO {
    @Length(min = 5, max = 160, message = "Email must be at least 5 and maximum 160 characters")
    @Email(message = "Email is invalid")
    private String email;
    @Length(min = 6, max = 160, message = "Password must be at least 6 and maximum 160 characters")
    private String password;
}
