package com.dozycoffee.admin.domain;

public enum AdminStatus {
    PENDING,
    ACTIVE,
    INACTIVE,
    ;

    public static AdminStatus of(String code) {
        for (AdminStatus status : AdminStatus.values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new AdminException("Unknown admin status: " + code);
    }
}