package com.dozycoffee.auth.application;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.security.SessionInvalidationPort;

import java.util.HashSet;
import java.util.Set;

public class FakeSessionInvalidationPort implements SessionInvalidationPort {

    private final Set<Principal> invalidated = new HashSet<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public boolean wasInvalidated(Principal principal) {
        return invalidated.contains(principal);
    }

    @Override
    public void invalidate(Principal principal) {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
        invalidated.add(principal);
    }
}
