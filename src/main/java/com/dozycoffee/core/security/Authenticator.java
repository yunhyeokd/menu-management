package com.dozycoffee.core.security;

import com.dozycoffee.core.id.Identifier;

import java.util.Optional;

public interface Authenticator<T extends Identifier<?>> {
    Optional<Principal> authenticate(T id, Credential credential);
}
