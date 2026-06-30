package com.dozycoffee.infrastructure.security;

import com.dozycoffee.auth.application.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordHasher passwordHasher() {
        return new Argon2PasswordHasher();
    }



}
