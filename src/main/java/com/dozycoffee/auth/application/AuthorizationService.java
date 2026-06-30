package com.dozycoffee.auth.application;

import org.springframework.stereotype.Service;

import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.application.exception.AuthorizationException;

import java.util.List;

@Service
public class AuthorizationService {

    public void authorize(Principal principal, List<String> roles) {
        if (roles.stream().noneMatch(role -> principal.getRole().equals(role))) {
            throw new AuthorizationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHORIZED);
        }
    }
}
