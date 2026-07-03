package com.dozycoffee.auth.presentation.dto;

import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.domain.SessionId;

public record AuthLoginResponse(
        String sessionId,
        String subject,
        String role
) {

    public static AuthLoginResponse of(SessionId sessionId, Principal principal) {
        return new AuthLoginResponse(sessionId.getValue(), principal.getSubject(), principal.getRole());
    }
}
