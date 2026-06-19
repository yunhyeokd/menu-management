package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.common.Identifier;

public interface Authenticator {
    void authenticate(Credential credential, Identifier<?> id);
}
