package com.dozycoffee.core.security;

public interface SessionInvalidationPort {
    void invalidate(Principal principal);
}
