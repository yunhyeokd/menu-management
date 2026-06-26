package com.dozycoffee.core.application;

import com.dozycoffee.core.domain.Session;
import com.dozycoffee.core.domain.SessionId;

import java.util.Optional;

public interface SessionManager<T> {
    Session<T> create(T context, long ttl);
    Optional<Session<T>> find(SessionId sessionId);
    void invalidate(SessionId sessionId);
}
