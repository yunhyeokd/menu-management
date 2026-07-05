package com.dozycoffee.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank String role,
        @NotBlank String id,
        @NotBlank String credential
) {
}
