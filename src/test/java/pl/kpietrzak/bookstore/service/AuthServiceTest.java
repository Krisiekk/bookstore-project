package pl.kpietrzak.bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kpietrzak.bookstore.dto.auth.AuthResponse;
import pl.kpietrzak.bookstore.dto.auth.LoginRequest;
import pl.kpietrzak.bookstore.dto.auth.RegisterRequest;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.repository.UserRepository;
import pl.kpietrzak.bookstore.security.CustomUserDetailsService;
import pl.kpietrzak.bookstore.security.JwtService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                customUserDetailsService,
                authenticationManager
        );
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@test.pl");
        request.setPassword("secret123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("user1");
        savedUser.setEmail("user1@test.pl");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.USER);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.pl")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(customUserDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getEmail()).isEqualTo("user1@test.pl");
        assertThat(response.getRole()).isEqualTo(Role.USER);

        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByEmail("user1@test.pl");
        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@test.pl");
        request.setPassword("secret123");

        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already in use");

        verify(userRepository).existsByUsername("user1");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@test.pl");
        request.setPassword("secret123");

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.pl")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already in use");

        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByEmail("user1@test.pl");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@bookstore.com");
        user.setPassword("encoded-password");
        user.setRole(Role.ADMIN);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("admin-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("admin-jwt-token");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getEmail()).isEqualTo("admin@bookstore.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername("admin");
        verify(customUserDetailsService).loadUserByUsername("admin");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void shouldThrowExceptionWhenLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("missing");
        request.setPassword("password");

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername("missing");
        verify(jwtService, never()).generateToken(any());
    }
}