package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.common.Identifier;

import java.util.Optional;

public interface Authenticator<T extends Identifier<?>> {
    Optional<Principal> authenticate(T id, Credential credential);
}
