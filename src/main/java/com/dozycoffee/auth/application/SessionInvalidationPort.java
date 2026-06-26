package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Principal;

public interface SessionInvalidationPort {
    void invalidate(Principal principal);
}
