package com.dozycoffee.admin.application.dto;

import com.dozycoffee.admin.domain.AdminId;

public record AdminProfileUpdateResult(
        AdminId adminId,
        String name,
        String phone,
        String email
) {
}
