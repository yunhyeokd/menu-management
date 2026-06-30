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
        AdminRole adminRole = AdminRole.of(role);
        AdminProfile profile = adminRole != AdminRole.SYSTEM
                ? AdminProfile.of(employeeNo, name, phone, email)
                : null;
        return Admin.of(
                AdminId.of(adminId),
                adminRole,
                username,
                passwordHash,
                AdminStatus.of(status),
                createdAt,
                deletedAt,
                profile
        );
    }

}
