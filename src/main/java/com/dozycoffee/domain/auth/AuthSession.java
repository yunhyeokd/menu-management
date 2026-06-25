package com.dozycoffee.domain.auth;

import com.dozycoffee.domain.common.Identifier;

public class AuthSession {

    private SessionId sessionId;
    private Principal principal;

    private AuthSession(SessionId sessionId, Principal principal) {
        setSessionId(sessionId);
        setPrincipal(principal);
    }

    public static AuthSession of(SessionId sessionId, Principal principal) {
        return new AuthSession(sessionId, principal);
    }

    public static AuthSession create(SessionId sessionId, Principal principal) {
        return new AuthSession(sessionId, principal);
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

}
