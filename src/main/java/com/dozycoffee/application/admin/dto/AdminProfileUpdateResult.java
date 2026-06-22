package com.dozycoffee.application.admin.dto;

import com.dozycoffee.domain.admin.AdminId;

public record AdminProfileUpdateResult(
        AdminId adminId,
        String name,
        String phone,
        String email
) {
}
