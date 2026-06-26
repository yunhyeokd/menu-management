package com.dozycoffee.auth.domain;

import java.time.Instant;

public class AuthSession {

    private SessionId sessionId;
    private Principal principal;
    private Instant expiresAt;

    private AuthSession(SessionId sessionId, Principal principal, Instant expiresAt) {
        setSessionId(sessionId);
        setPrincipal(principal);
        setExpiresAt(expiresAt);
    }

    public static AuthSession of(SessionId sessionId, Principal principal, Instant expiresAt) {
        return new AuthSession(sessionId, principal, expiresAt);
    }

    public static AuthSession create(SessionId sessionId, Principal principal, Instant expiresAt) {
        return new AuthSession(sessionId, principal, expiresAt);
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    private void setSessionId(SessionId sessionId) {
        if (sessionId == null) {
            throw new AuthException("sessionId is null");
        }
        this.sessionId = sessionId;
    }

    public Principal getPrincipal() {
        return principal;
    }

    private void setPrincipal(Principal principal) {
        if (principal == null) {
            throw new AuthException("principal is null");
        }
        this.principal = principal;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    private void setExpiresAt(Instant expiresAt) {
        if (expiresAt == null) {
            throw new AuthException("expiresAt is null");
        }
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void extendExpiry(Instant expiresAt) {
        setExpiresAt(expiresAt);
    }

}
