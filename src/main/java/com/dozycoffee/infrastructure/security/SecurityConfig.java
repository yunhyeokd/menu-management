package com.dozycoffee.infrastructure.security;

import com.dozycoffee.admin.application.AdminUsernameAuthenticator;
import com.dozycoffee.auth.application.AuthenticationService;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.branch.application.BranchAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordHasher passwordHasher() {
        return new PasswordHasher() {
            final Argon2PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

            @Override
            public String hash(String raw) {
                return passwordEncoder.encode(raw);
            }

            @Override
            public boolean matches(String raw, String hash) {
                return passwordEncoder.matches(raw, hash);
            }
        };
    }

    @Bean
    public AuthenticationService authenticationService(
            AdminUsernameAuthenticator adminAuthenticator,
            BranchAuthenticator branchAuthenticator
    ) {
        return AuthenticationResolverFactory.create(adminAuthenticator, branchAuthenticator);
    }

}
