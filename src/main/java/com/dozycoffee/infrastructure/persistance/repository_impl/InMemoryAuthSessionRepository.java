package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.auth.application.AuthSessionRepository;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.core.session.SessionId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAuthSessionRepository implements AuthSessionRepository {

    private final Map<String, Session<Principal>> store = new ConcurrentHashMap<>();

    @Override
    public void save(Session<Principal> session) throws RepositoryException {
        store.put(session.getSessionId().getValue(), session);
    }

    @Override
    public Optional<Session<Principal>> findById(SessionId sessionId) throws RepositoryException {
        return Optional.ofNullable(store.get(sessionId.getValue()));
    }

    @Override
    public void deleteById(SessionId sessionId) throws RepositoryException {
        store.remove(sessionId.getValue());
    }

    @Override
    public void deleteAllByPrincipal(Principal principal) throws RepositoryException {
        store.values().removeIf(session ->
                session.getContext().getSubject().equals(principal.getSubject())
                && session.getContext().getRole().equals(principal.getRole())
        );
    }
}
