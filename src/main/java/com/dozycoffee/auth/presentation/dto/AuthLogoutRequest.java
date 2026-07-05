package com.dozycoffee.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLogoutRequest(
        @NotBlank String sessionId
) {
}
