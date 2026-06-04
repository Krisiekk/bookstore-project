package pl.kpietrzak.bookstore.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequest {
    @NotBlank(message="username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

}
