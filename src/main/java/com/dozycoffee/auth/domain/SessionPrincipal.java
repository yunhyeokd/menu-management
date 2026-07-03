package com.dozycoffee.auth.domain;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o instanceof Principal principal) {
            return Objects.equals(getSubject(), principal.getSubject())
                    && Objects.equals(getRole(), principal.getRole());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
