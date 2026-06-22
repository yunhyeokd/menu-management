package com.dozycoffee.application.admin.dto;

import com.dozycoffee.domain.admin.AdminId;
import com.dozycoffee.domain.admin.AdminRole;

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
