package com.dozycoffee.auth.presentation.dto;

public record AuthLoginRequest(
        String role,
        String id,
        String credential
) {
}
