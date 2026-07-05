package com.dozycoffee.auth.application;

import com.dozycoffee.core.security.Credential;

public interface AuthenticationResolver {
    AuthenticationResult resolve(String id, Credential credential);
}
