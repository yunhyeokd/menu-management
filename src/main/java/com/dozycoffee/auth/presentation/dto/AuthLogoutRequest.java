package com.dozycoffee.auth.presentation.dto;

public record AuthLogoutRequest(
        String sessionId
) {
}
