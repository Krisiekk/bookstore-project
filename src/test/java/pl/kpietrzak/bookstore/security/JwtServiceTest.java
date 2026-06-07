package pl.kpietrzak.bookstore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "jwtSecret",
                "0123456789012345678901234567890123456789012345678901234567890123"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "jwtExpiration",
                86400000L
        );

        userDetails = User
                .withUsername("admin")
                .password("encoded-password")
                .authorities("ROLE_ADMIN")
                .build();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("admin");
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);

        boolean result = jwtService.isTokenValid(token, userDetails);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTokenBelongsToDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = User
                .withUsername("user1")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        boolean result = jwtService.isTokenValid(token, differentUser);

        assertThat(result).isFalse();
    }
}