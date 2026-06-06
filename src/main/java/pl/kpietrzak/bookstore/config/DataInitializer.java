package pl.kpietrzak.bookstore.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.Role;
import pl.kpietrzak.bookstore.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner crateDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args ->{
            String adminUsername = "admin";
            String adminEmail = "admin@bookstore.com";

            if(!userRepository.existsByUsername(adminUsername) && !userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }


        };

    }

}
