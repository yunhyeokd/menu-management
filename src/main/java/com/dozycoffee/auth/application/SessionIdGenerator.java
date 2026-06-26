package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;

public interface SessionIdGenerator {
    SessionId generate(Principal principal);
}
