package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.auth.application.AuthSessionRepository;
import com.dozycoffee.core.security.SessionInvalidationPort;
import com.dozycoffee.core.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionInvalidationAdapter implements SessionInvalidationPort {

    private final AuthSessionRepository authSessionRepository;

    @Override
    public void invalidate(Principal principal) {
        authSessionRepository.deleteAllByPrincipal(principal);
    }
}
