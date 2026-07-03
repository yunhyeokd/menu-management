package com.dozycoffee.admin.presentation.dto;

public record AdminPasswordChangeRequest(
        String currentPassword,
        String newPassword
) {
}
