package com.dozycoffee.domain.auth;

import com.dozycoffee.domain.common.Identifier;

public class SessionPrincipal implements Principal {

    private String id;
    private String role;

    private SessionPrincipal(String id, String role) {
        setId(id);
        setRole(role);
    }

    public static SessionPrincipal of(String id, String role) {
        return new SessionPrincipal(id, role);
    }

    private void setId(String id) {
        if (id == null) {
            throw new AuthException("id cannot be null");
        }
        this.id = id;
    }

    @Override
    public String getSubject() {
        return id;
    }

    @Override
    public String getRole() {
        return role;
    }

    private void setRole(String role) {
        if (role == null) {
            throw new AuthException("role cannot be null");
        }
        this.role = role;
    }
}
