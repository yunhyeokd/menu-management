package com.dozycoffee.core.session;

import com.dozycoffee.core.exception.DomainException;

import java.time.Instant;

public class Session<T> {

    private final SessionId sessionId;
    private final T context;
    private Instant expiresAt;

    private Session(SessionId sessionId, T context, Instant expiresAt) {
        if (sessionId == null) throw new DomainException("sessionId must not be null");
        if (context == null) throw new DomainException("context must not be null");
        if (expiresAt == null) throw new DomainException("expiresAt must not be null");
        this.sessionId = sessionId;
        this.context = context;
        this.expiresAt = expiresAt;
    }

    public static <T> Session<T> create(SessionId sessionId, T context, Instant expiresAt) {
        return new Session<>(sessionId, context, expiresAt);
    }

    public static <T> Session<T> of(SessionId sessionId, T context, Instant expiresAt) {
        return new Session<>(sessionId, context, expiresAt);
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public T getContext() {
        return context;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void extendExpiry(Instant expiresAt) {
        if (expiresAt == null) throw new DomainException("expiresAt must not be null");
        this.expiresAt = expiresAt;
    }
}
