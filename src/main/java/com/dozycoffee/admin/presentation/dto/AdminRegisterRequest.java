package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.application.dto.AdminRegisterCommand;

public record AdminRegisterRequest(
        String username,
        String password,
        String employeeNo,
        String name,
        String phone,
        String email
) {

    public AdminRegisterCommand toCommand() {
        return new AdminRegisterCommand(username, password, employeeNo, name, phone, email);
    }
}
