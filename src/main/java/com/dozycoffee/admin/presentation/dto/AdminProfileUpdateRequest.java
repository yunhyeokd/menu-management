package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.application.dto.AdminProfileUpdateCommand;

public record AdminProfileUpdateRequest(
        String name,
        String phone,
        String email
) {

    public AdminProfileUpdateCommand toCommand() {
        return new AdminProfileUpdateCommand(name, phone, email);
    }
}
