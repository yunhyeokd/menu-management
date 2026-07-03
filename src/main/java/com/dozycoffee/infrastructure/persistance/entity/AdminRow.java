package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.admin.domain.*;

import java.time.Instant;

public record AdminRow(
        String adminId,
        String role,
        String status,
        String username,
        String passwordHash,
        Instant deletedAt,
        Instant createdAt,
        String employeeNo,
        String name,
        String phone,
        String email
) {
    public Admin toAdmin() {
        AdminProfile profile = AdminProfile.of(employeeNo, name, phone, email);
        return Admin.of(
                AdminId.of(adminId),
                username,
                passwordHash,
                AdminStatus.of(status),
                createdAt,
                deletedAt,
                profile
        );
    }

    public SystemAdmin toSystemAdmin() {
        return SystemAdmin.of(
                AdminId.of(adminId),
                username,
                passwordHash,
                AdminStatus.of(status),
                createdAt,
                deletedAt
        );
    }

    public AdminPrincipal toPrincipal() {
        AdminRole adminRole = AdminRole.of(role);
        if (adminRole == AdminRole.SYSTEM) return toSystemAdmin();
        else return toAdmin();
    }

}
