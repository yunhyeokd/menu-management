package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Credential;

public interface AuthenticationResolver {
    AuthenticationResult resolve(String id, Credential credential);
}
