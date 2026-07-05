package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.application.dto.AdminRegisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminRegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String employeeNo,
        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank @Email String email
) {

    public AdminRegisterCommand toCommand() {
        return new AdminRegisterCommand(username, password, employeeNo, name, phone, email);
    }
}
