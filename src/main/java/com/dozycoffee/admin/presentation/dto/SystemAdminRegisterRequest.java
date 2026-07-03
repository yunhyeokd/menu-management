package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.application.dto.SystemAdminRegisterCommand;

public record SystemAdminRegisterRequest(
        String username,
        String password
) {

    public SystemAdminRegisterCommand toCommand() {
        return new SystemAdminRegisterCommand(username, password);
    }
}
