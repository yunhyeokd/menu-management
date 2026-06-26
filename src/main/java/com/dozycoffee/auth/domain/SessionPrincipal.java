package com.dozycoffee.auth.domain;

public class SessionPrincipal implements Principal {

    private final String id;
    private final String role;

    private SessionPrincipal(String id, String role) {
        if (id == null) throw new AuthException("SessionPrincipal id cannot be null");
        if (role == null) throw new AuthException("SessionPrincipal role cannot be null");
        this.id = id;
        this.role = role;
    }

    public static SessionPrincipal of(String id, String role) {
        return new SessionPrincipal(id, role);
    }

    @Override
    public String getSubject() {
        return id;
    }

    @Override
    public String getRole() {
        return role;
    }
}
