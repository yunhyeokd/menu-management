package com.dozycoffee.admin.application.dto;

import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.time.Instant;

public record SystemAdminRegisterResult(
        AdminId adminId,
        AdminRole adminRole,
        String username,
        Instant createdAt
) {
}
