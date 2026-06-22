package com.dozycoffee.application.admin.dto;

import com.dozycoffee.domain.admin.AdminId;
import com.dozycoffee.domain.admin.AdminRole;

import java.time.Instant;

public record SystemAdminRegisterResult(
        AdminId adminId,
        AdminRole adminRole,
        String username,
        Instant createdAt
) {
}
