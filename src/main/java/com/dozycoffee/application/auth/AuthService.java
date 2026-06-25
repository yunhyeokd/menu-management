package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.AuthSession;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.auth.SessionId;
import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.common.Identifier;

import java.util.List;
import java.util.Optional;

public class AuthService implements SessionInvalidationPort {

    private final AuthSessionRepository authSessionRepository;
    private final Authenticator<Identifier<String>> adminAuthenticator;
    private final Authenticator<BranchCode> branchAuthenticator;
    private final SessionIdGenerator sessionIdGenerator;

    public AuthService(
            AuthSessionRepository authSessionRepository,
            Authenticator<Identifier<String>> adminAuthenticator,
            Authenticator<BranchCode> branchAuthenticator, SessionIdGenerator sessionIdGenerator
    ) {
        this.authSessionRepository = authSessionRepository;
        this.adminAuthenticator = adminAuthenticator;
        this.branchAuthenticator = branchAuthenticator;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    public void invalidate(Principal principal) {
        authSessionRepository.deleteAllByPrincipal(principal);
    }

    public void invalidate(SessionId sessionId) {
        authSessionRepository.deleteById(sessionId);
    }

    public AuthSession authenticateAdmin(String username, String password) {
        Identifier<String> id = () -> username;
        Credential credential = () -> password;
        Principal principal = adminAuthenticator.authenticate(id, credential)
                .orElseThrow(() -> AuthBusinessException.with(AuthErrors.INVALID_CREDENTIAL));
        AuthSession authSession = AuthSession.create(
                sessionIdGenerator.generate(principal),
                principal
        );
        authSessionRepository.save(authSession);
        return authSession;
    }

    public AuthSession authenticateBranch(String code, String authKey) {
        BranchCode id = BranchCode.of(code);
        Credential credential = () -> authKey;
        Principal principal = branchAuthenticator.authenticate(id, credential)
                .orElseThrow(() -> AuthBusinessException.with(AuthErrors.INVALID_CREDENTIAL));
        AuthSession authSession = AuthSession.create(
                sessionIdGenerator.generate(principal),
                principal
        );
        authSessionRepository.save(authSession);
        return authSession;
    }

    public Principal requirePrincipal(SessionId sessionId) {
        return authSessionRepository.findById(sessionId)
                .orElseThrow(() -> AuthBusinessException.with(AuthErrors.UNAUTHENTICATED));
    }

    public Optional<Principal> findPrincipal(SessionId sessionId) {
        return authSessionRepository.findById(sessionId);
    }

    public void authorize(Principal principal, List<String> roles) {
        if (roles.stream().noneMatch(role -> principal.getRole().equals(role))) {
            throw AuthBusinessException.with(AuthErrors.UNAUTHORIZED);
        }
    }

}
