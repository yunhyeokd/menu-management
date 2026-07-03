package com.dozycoffee.admin.domain;

import java.time.Instant;

public class SystemAdmin extends AdminPrincipal {

    private SystemAdmin(
            AdminId id,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        super(id, AdminRole.SYSTEM, username, passwordHash, status, createdAt, deletedAt);
    }

    public static SystemAdmin of(
            AdminId id,
            String username,
            String password,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        return new SystemAdmin(id, username, password, status, createdAt, deletedAt);
    }

    public static SystemAdmin create(AdminId id, String username, String password) {
        return new SystemAdmin(id, username, password, AdminStatus.ACTIVE, Instant.now(), null);
    }

}
