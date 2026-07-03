package com.dozycoffee.infrastructure.security;

import com.dozycoffee.admin.application.AdminUsernameAuthenticator;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.auth.application.AuthenticationResolver;
import com.dozycoffee.auth.application.AuthenticationResult;
import com.dozycoffee.auth.application.AuthenticationService;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.branch.application.BranchAuthenticator;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.core.application.exception.AuthenticationException;
import com.dozycoffee.core.domain.Identifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Map;

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
        AuthenticationResolver systemResolver = adminResolver(adminAuthenticator, AdminRole.SYSTEM);
        AuthenticationResolver adminResolver = adminResolver(adminAuthenticator, AdminRole.ADMIN);
        AuthenticationResolver branchResolver = (id, credential) -> {
            Principal principal = branchAuthenticator.authenticate(BranchCode.of(id), credential)
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, 86400L);
        };

        return new AuthenticationService(Map.of(
                "SYSTEM", systemResolver,
                "ADMIN", adminResolver,
                "BRANCH", branchResolver
        ));
    }

    private AuthenticationResolver adminResolver(AdminUsernameAuthenticator adminAuthenticator, AdminRole expectedRole) {
        return (id, credential) -> {
            Principal principal = adminAuthenticator.authenticate((Identifier<String>) () -> id, credential)
                    .filter(p -> p.getRole().equals(expectedRole.name()))
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, 3600L);
        };
    }

}
