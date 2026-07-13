package com.dozycoffee.core.security;

public class FakePasswordHasher implements PasswordHasher {

    @Override
    public String hash(String raw) {
        return "hashed-" + raw;
    }

    @Override
    public boolean matches(String raw, String hash) {
        return hash(raw).equals(hash);
    }
}
