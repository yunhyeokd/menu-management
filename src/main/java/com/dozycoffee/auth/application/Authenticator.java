package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.domain.Identifier;

import java.util.Optional;

public interface Authenticator<T extends Identifier<?>> {
    Optional<Principal> authenticate(T id, Credential credential);
}
