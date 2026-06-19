package com.dozycoffee.domain.auth;

public class PasswordCredential implements Credential {

    private final String hash;

    public PasswordCredential(String hash) {
        if (hash == null || hash.isBlank()) throw new IllegalArgumentException("hash cannot be blank");
        this.hash = hash;
    }

    @Override
    public String getValue() {
        return hash;
    }
}
