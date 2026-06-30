package com.dozycoffee.infrastructure.security;

import com.dozycoffee.auth.application.PasswordHasher;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Argon2PasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Override
    public String hash(String raw) {
        return passwordEncoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String hash) {
        return passwordEncoder.matches(raw, hash);
    }
}
