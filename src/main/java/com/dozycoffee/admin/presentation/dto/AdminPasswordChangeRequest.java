package com.dozycoffee.admin.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminPasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
}
