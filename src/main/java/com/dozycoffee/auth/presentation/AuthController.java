package com.dozycoffee.auth.presentation;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.auth.application.AuthenticationResult;
import com.dozycoffee.auth.application.AuthenticationService;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.auth.presentation.dto.AuthLoginRequest;
import com.dozycoffee.auth.presentation.dto.AuthLoginResponse;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.core.session.SessionId;
import com.dozycoffee.infrastructure.security.SessionPrincipalFilter;
import com.dozycoffee.infrastructure.web.resolver.AuthPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AuthSessionManager authSessionManager;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthenticationResult result = authenticationService.authenticate(
                request.role(), request.id(), request::credential
        );
        Session<Principal> session = authSessionManager.create(result.principal(), result.sessionTtl());
        return ResponseEntity.ok(AuthLoginResponse.of(session.getSessionId(), result.principal()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthPrincipal Principal principal, HttpServletRequest request) {
        SessionId sessionId = (SessionId) request.getAttribute(SessionPrincipalFilter.SESSION_ID_ATTRIBUTE);
        authSessionManager.invalidate(sessionId);
        return ResponseEntity.noContent().build();
    }
}
