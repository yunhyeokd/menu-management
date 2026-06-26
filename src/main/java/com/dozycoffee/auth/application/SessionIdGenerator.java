package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.auth.SessionId;

public interface SessionIdGenerator {
    SessionId generate(Principal principal);
}
