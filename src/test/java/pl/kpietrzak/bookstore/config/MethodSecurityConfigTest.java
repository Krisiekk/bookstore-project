package pl.kpietrzak.bookstore.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.assertj.core.api.Assertions.assertThat;

class MethodSecurityConfigTest {

    @Test
    void shouldEnableMethodSecurity() {
        assertThat(MethodSecurityConfig.class.isAnnotationPresent(Configuration.class)).isTrue();
        assertThat(MethodSecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class)).isTrue();
    }
}
