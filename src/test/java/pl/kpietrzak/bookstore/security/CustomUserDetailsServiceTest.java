package pl.kpietrzak.bookstore.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CustomUserDetailsService customUserDetailsService =
            new CustomUserDetailsService(userRepository);

    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(userRepository).findByUsername("admin");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username: missing");

        verify(userRepository).findByUsername("missing");
    }
}