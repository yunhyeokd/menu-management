package com.dozycoffee.core.session;

import java.util.Optional;

public interface SessionManager<T> {
    Session<T> create(T context, long ttl);
    Optional<Session<T>> find(SessionId sessionId);
    void invalidate(SessionId sessionId);
}
