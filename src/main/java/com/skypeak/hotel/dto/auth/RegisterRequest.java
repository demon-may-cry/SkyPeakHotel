package com.skypeak.hotel.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class RegisterRequest {

    @NonNull
    @Email
    @Size(max = 100)
    private String email;

    @NonNull
    @Size(min = 8, max = 100)
    private String password;
}
