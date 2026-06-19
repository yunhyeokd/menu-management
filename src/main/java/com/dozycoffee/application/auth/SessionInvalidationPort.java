package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.Principal;

public interface SessionInvalidationPort {
    void invalidate(Principal principal);
}
