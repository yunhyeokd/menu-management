package com.dozycoffee.infrastructure.security;

import com.dozycoffee.admin.application.AdminUsernameAuthenticator;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.auth.application.AuthenticationResolver;
import com.dozycoffee.auth.application.AuthenticationResult;
import com.dozycoffee.auth.application.AuthenticationService;
import com.dozycoffee.branch.application.BranchAuthenticator;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.core.exception.service.AuthenticationException;
import com.dozycoffee.core.id.Identifier;
import com.dozycoffee.core.security.Principal;

import java.util.Map;

final class AuthenticationResolverFactory {

    private static final long ADMIN_SESSION_TTL_SECONDS = 3600L;
    private static final long BRANCH_SESSION_TTL_SECONDS = 86400L;

    private AuthenticationResolverFactory() {
    }

    static AuthenticationService create(
            AdminUsernameAuthenticator adminAuthenticator,
            BranchAuthenticator branchAuthenticator
    ) {
        return new AuthenticationService(Map.of(
                "SYSTEM", adminResolver(adminAuthenticator, AdminRole.SYSTEM),
                "ADMIN", adminResolver(adminAuthenticator, AdminRole.ADMIN),
                "BRANCH", branchResolver(branchAuthenticator)
        ));
    }

    private static AuthenticationResolver adminResolver(AdminUsernameAuthenticator adminAuthenticator, AdminRole expectedRole) {
        return (id, credential) -> {
            Principal principal = adminAuthenticator.authenticate((Identifier<String>) () -> id, credential)
                    .filter(p -> p.getRole().equals(expectedRole.name()))
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, ADMIN_SESSION_TTL_SECONDS);
        };
    }

    private static AuthenticationResolver branchResolver(BranchAuthenticator branchAuthenticator) {
        return (id, credential) -> {
            Principal principal = branchAuthenticator.authenticate(BranchCode.of(id), credential)
                    .orElseThrow(() -> new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHENTICATED));
            return new AuthenticationResult(principal, BRANCH_SESSION_TTL_SECONDS);
        };
    }
}
