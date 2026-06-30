package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.auth.application.AuthSessionRepository;
import com.dozycoffee.auth.application.SessionInvalidationPort;
import com.dozycoffee.auth.domain.Principal;
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
