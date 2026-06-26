package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.AuthSession;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;
import com.dozycoffee.core.application.exception.AuthenticationException;

import java.time.Instant;
import java.util.Optional;

public class SessionManager implements SessionInvalidationPort {

    private final AuthSessionRepository authSessionRepository;
    private final SessionIdGenerator sessionIdGenerator;

    public SessionManager(AuthSessionRepository authSessionRepository, SessionIdGenerator sessionIdGenerator) {
        this.authSessionRepository = authSessionRepository;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public AuthSession create(Principal principal, long ttl) {
        AuthSession session = AuthSession.create(
                sessionIdGenerator.generate(principal),
                principal,
                Instant.now().plusSeconds(ttl)
        );
        authSessionRepository.save(session);
        return session;
    }

    @Override
    public void invalidate(Principal principal) {
        authSessionRepository.deleteAllByPrincipal(principal);
    }

    public void invalidate(SessionId sessionId) {
        authSessionRepository.deleteById(sessionId);
    }

    public Principal requirePrincipal(SessionId sessionId) {
        AuthSession authSession = authSessionRepository.findById(sessionId).orElse(null);
        if (authSession == null) throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED);
        if (authSession.isExpired()) throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.SESSION_EXPIRED);
        return authSession.getPrincipal();
    }

    public Optional<Principal> findPrincipal(SessionId sessionId) {
        AuthSession authSession = authSessionRepository.findById(sessionId).orElse(null);
        if (authSession == null) return Optional.empty();
        if (authSession.isExpired()) return Optional.empty();
        return Optional.of(authSession.getPrincipal());
    }
}
