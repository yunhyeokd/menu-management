package com.dozycoffee.admin.domain;

public enum AdminRole {
    SYSTEM,
    ADMIN,
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