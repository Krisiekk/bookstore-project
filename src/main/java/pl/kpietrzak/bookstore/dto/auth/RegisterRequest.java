package pl.kpietrzak.bookstore.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Size(min =3,max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message="Email should be valid")
    @Size(max = 150, message = "Email must be less than 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,max =100, message = "Password must be between 6 and 100 characters")
    private String password;


}
