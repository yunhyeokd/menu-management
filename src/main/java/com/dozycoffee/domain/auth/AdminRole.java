package com.dozycoffee.domain.auth;

public enum AdminRole {
    SYSTEM,
    STAFF,
    ;

    public static AdminRole of(String code) {
        for (AdminRole role : AdminRole.values()) {
            if (role.name().equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new AuthException("Unknown admin role: " + code);
    }
}