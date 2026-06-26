package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.core.application.exception.AuthenticationException;

import java.util.Map;
import java.util.Optional;

public class AuthenticationService {

    private final Map<String, AuthenticationResolver> resolvers;

    public AuthenticationService(Map<String, AuthenticationResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public AuthenticationResult authenticate(String role, String id, Credential credential) {
        AuthenticationResolver resolver = Optional.ofNullable(resolvers.get(role))
                .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNSUPPORTED_ROLE));
        return resolver.resolve(id, credential);
    }
}
