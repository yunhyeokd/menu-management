package com.dozycoffee.infrastructure.web.interceptor;

import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.auth.application.AuthorizationService;
import com.dozycoffee.core.exception.service.AuthenticationException;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.infrastructure.security.SessionPrincipalFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private final AuthorizationService authorizationService;

    public RoleAuthorizationInterceptor(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return true;
        }

        Principal principal = (Principal) request.getAttribute(SessionPrincipalFilter.PRINCIPAL_ATTRIBUTE);
        if (principal == null) {
            throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED);
        }

        authorizationService.authorize(principal, List.of(requireRole.value()));
        return true;
    }
}
