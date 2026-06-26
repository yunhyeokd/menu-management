package com.dozycoffee.admin.application.dto;

public record SystemAdminRegisterCommand(
        String username,
        String password
) {
}
