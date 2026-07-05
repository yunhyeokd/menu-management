package com.dozycoffee.infrastructure.web.resolver;

import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.core.exception.service.AuthenticationException;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.infrastructure.security.SessionPrincipalFilter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class)
                && Principal.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Object principal = webRequest.getAttribute(SessionPrincipalFilter.PRINCIPAL_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (principal == null) {
            throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED);
        }
        return principal;
    }
}
