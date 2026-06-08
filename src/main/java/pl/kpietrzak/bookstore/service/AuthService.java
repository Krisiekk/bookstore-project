package pl.kpietrzak.bookstore.service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kpietrzak.bookstore.dto.auth.AuthResponse;
import pl.kpietrzak.bookstore.dto.auth.LoginRequest;
import pl.kpietrzak.bookstore.dto.auth.RegisterRequest;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.repository.UserRepository;
import pl.kpietrzak.bookstore.security.CustomUserDetailsService;
import pl.kpietrzak.bookstore.security.JwtService;

/**
 * Service responsible for user registration, login, password encoding, and JWT generation.
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final   PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final  AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user with the default USER role and returns an authentication token.
     *
     * @param request registration data
     * @return authentication response containing JWT and user data
     */
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());


    }

    /**
     * Authenticates an existing user and returns a new JWT token.
     *
     * @param request login credentials
     * @return authentication response containing JWT and user data
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new IllegalArgumentException("Invalid username or password"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());


    }

}
