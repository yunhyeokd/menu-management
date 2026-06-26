package com.dozycoffee.auth.domain;

import com.dozycoffee.core.domain.Identifier;

import java.util.Optional;

public interface Authenticator<T extends Identifier<?>> {
    Optional<Principal> authenticate(T id, Credential credential);
}
