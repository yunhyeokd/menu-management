package com.dozycoffee.domain.admin;

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
        throw new AdminException("Unknown admin role: " + code);
    }
}