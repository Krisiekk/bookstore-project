package pl.kpietrzak.bookstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kpietrzak.bookstore.dto.auth.AuthResponse;
import pl.kpietrzak.bookstore.dto.auth.LoginRequest;
import pl.kpietrzak.bookstore.dto.auth.RegisterRequest;
import pl.kpietrzak.bookstore.service.AuthService;
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);


    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}