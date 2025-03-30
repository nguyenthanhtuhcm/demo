package com.example.demo.configuration;

import com.example.demo.entity.User;
import com.example.demo.enums.Roles;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppInitConfig {
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            var user = userRepository.findByUsername("admin");
            if (user.isEmpty()) {
                // Create a default user if it doesn't exist
                HashSet<String> _roles = new HashSet<>();
                _roles.add(Roles.ADMIN.name());
                var _user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(_roles)
                        .build();

                userRepository.save(_user);

            }
        };
    }
}
