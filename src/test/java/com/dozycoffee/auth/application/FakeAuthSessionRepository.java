package com.dozycoffee.auth.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.auth.domain.AuthSession;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAuthSessionRepository implements AuthSessionRepository {

    private final Map<SessionId, AuthSession> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public int size() {
        return store.size();
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(AuthSession authSession) {
        checkThrow();
        store.put(authSession.getSessionId(), authSession);
    }

    @Override
    public Optional<AuthSession> findById(SessionId sessionId) {
        checkThrow();
        return Optional.ofNullable(store.get(sessionId));
    }

    @Override
    public void deleteById(SessionId sessionId) {
        checkThrow();
        store.remove(sessionId);
    }

    @Override
    public void deleteAllByPrincipal(Principal principal) {
        checkThrow();
        store.values().removeIf(session ->
                session.getPrincipal().getSubject().equals(principal.getSubject()) &&
                session.getPrincipal().getRole().equals(principal.getRole())
        );
    }
}
