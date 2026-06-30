package com.dozycoffee.auth.application;

import org.springframework.stereotype.Service;

import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.application.SessionManager;
import com.dozycoffee.core.application.exception.AuthenticationException;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.domain.Session;
import com.dozycoffee.core.domain.SessionId;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthSessionManager implements SessionManager<Principal> {

    private final AuthSessionRepository authSessionRepository;
    private final IdentifierGenerator<SessionId> sessionIdGenerator;

    public AuthSessionManager(AuthSessionRepository authSessionRepository, IdentifierGenerator<SessionId> sessionIdGenerator) {
        this.authSessionRepository = authSessionRepository;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    public Session<Principal> create(Principal principal, long ttl) {
        Session<Principal> session = Session.create(
                sessionIdGenerator.generate(),
                principal,
                Instant.now().plusSeconds(ttl)
        );
        authSessionRepository.save(session);
        return session;
    }

    @Override
    public Optional<Session<Principal>> find(SessionId sessionId) {
        return authSessionRepository.findById(sessionId)
                .filter(s -> !s.isExpired());
    }

    @Override
    public void invalidate(SessionId sessionId) {
        authSessionRepository.deleteById(sessionId);
    }

    public Principal requirePrincipal(SessionId sessionId) {
        Session<Principal> session = authSessionRepository.findById(sessionId).orElse(null);
        if (session == null) throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED);
        if (session.isExpired()) throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.SESSION_EXPIRED);
        return session.getContext();
    }

    public Optional<Principal> findPrincipal(SessionId sessionId) {
        return find(sessionId).map(Session::getContext);
    }
}
