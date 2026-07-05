package com.dozycoffee.auth.application;

import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.core.session.SessionId;

import java.util.Optional;

public interface AuthSessionRepository {
    void save(Session<Principal> session) throws RepositoryException;
    Optional<Session<Principal>> findById(SessionId sessionId) throws RepositoryException;
    void deleteById(SessionId sessionId) throws RepositoryException;
    void deleteAllByPrincipal(Principal principal) throws RepositoryException;
}
