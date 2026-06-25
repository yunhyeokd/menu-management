package com.dozycoffee.application.auth;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.auth.AuthSession;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.auth.SessionId;

import java.util.Optional;

public interface AuthSessionRepository {
    void save(AuthSession authSession) throws RepositoryException;
    Optional<Principal> findById(SessionId sessionId) throws RepositoryException;
    void deleteById(SessionId sessionId) throws RepositoryException;
    void deleteAllByPrincipal(Principal principal) throws RepositoryException;
}
