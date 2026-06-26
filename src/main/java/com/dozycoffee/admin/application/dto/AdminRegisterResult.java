package com.dozycoffee.admin.application.dto;

import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.time.Instant;

public record AdminRegisterResult(
        AdminId adminId,
        AdminRole adminRole,
        String username,
        String name,
        String email,
        Instant createdAt
) {
}
