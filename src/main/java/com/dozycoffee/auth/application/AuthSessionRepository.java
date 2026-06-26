package com.dozycoffee.auth.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.auth.domain.AuthSession;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;

import java.util.Optional;

public interface AuthSessionRepository {
    void save(AuthSession authSession) throws RepositoryException;
    Optional<AuthSession> findById(SessionId sessionId) throws RepositoryException;
    void deleteById(SessionId sessionId) throws RepositoryException;
    void deleteAllByPrincipal(Principal principal) throws RepositoryException;
}
