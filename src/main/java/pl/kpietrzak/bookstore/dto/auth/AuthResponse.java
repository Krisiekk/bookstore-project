package pl.kpietrzak.bookstore.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kpietrzak.bookstore.enums.Role;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private Role role;


}
