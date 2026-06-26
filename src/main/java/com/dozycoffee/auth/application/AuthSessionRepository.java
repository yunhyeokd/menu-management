package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.domain.Session;
import com.dozycoffee.core.domain.SessionId;

import java.util.Optional;

public interface AuthSessionRepository {
    void save(Session<Principal> session) throws RepositoryException;
    Optional<Session<Principal>> findById(SessionId sessionId) throws RepositoryException;
    void deleteById(SessionId sessionId) throws RepositoryException;
    void deleteAllByPrincipal(Principal principal) throws RepositoryException;
}
