package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.application.dto.AdminProfileUpdateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminProfileUpdateRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank @Email String email
) {

    public AdminProfileUpdateCommand toCommand() {
        return new AdminProfileUpdateCommand(name, phone, email);
    }
}
