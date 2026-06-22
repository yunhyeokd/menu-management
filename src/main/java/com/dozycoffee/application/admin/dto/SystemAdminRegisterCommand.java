package com.dozycoffee.application.admin.dto;

public record SystemAdminRegisterCommand(
        String username,
        String password
) {
}
