package com.dozycoffee.infrastructure.security;

import com.dozycoffee.auth.application.AuthSessionManager;
import com.dozycoffee.core.session.SessionId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component("sessionPrincipalFilter")
public class SessionPrincipalFilter extends OncePerRequestFilter {

    public static final String PRINCIPAL_ATTRIBUTE = SessionPrincipalFilter.class.getName() + ".PRINCIPAL";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthSessionManager authSessionManager;

    public SessionPrincipalFilter(AuthSessionManager authSessionManager) {
        this.authSessionManager = authSessionManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        extractSessionId(request)
                .flatMap(authSessionManager::findPrincipal)
                .ifPresent(principal -> request.setAttribute(PRINCIPAL_ATTRIBUTE, principal));
        filterChain.doFilter(request, response);
    }

    private Optional<SessionId> extractSessionId(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isBlank() ? Optional.empty() : Optional.of(SessionId.of(token));
    }
}
