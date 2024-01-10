package com.pt.dto.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
public class UpdateUserReqDTO {
    @Length(min = 5, max = 160, message = "Name must be at least 5 and maximum 160 characters")
    private String name;

    @Length(min = 6, max = 160, message = "Password must be at least 6 and maximum 160 characters")
    private String password;

    @Length(min = 6, max = 160, message = "New Password must be at least 6 and maximum 160 characters")
    private String new_password;
    private String phone;
    private LocalDate date_of_birth;
    private String address;
}
