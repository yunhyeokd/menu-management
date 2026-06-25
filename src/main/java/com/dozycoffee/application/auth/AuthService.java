package com.dozycoffee.application.auth;

import com.dozycoffee.domain.auth.AuthSession;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.auth.SessionId;
import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.common.Identifier;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AuthService implements SessionInvalidationPort {

    private final long ADMIN_SESSION_TTL = 60 * 60;
    private final long BRANCH_SESSION_TTL = 24 * 60 * 60;

    private final AuthSessionRepository authSessionRepository;
    private final Authenticator<Identifier<String>> adminUsernameAuthenticator;
    private final Authenticator<BranchCode> branchAuthenticator;
    private final SessionIdGenerator sessionIdGenerator;

    public AuthService(
            AuthSessionRepository authSessionRepository,
            Authenticator<Identifier<String>> adminUsernameAuthenticator,
            Authenticator<BranchCode> branchAuthenticator,
            SessionIdGenerator sessionIdGenerator
    ) {
        this.authSessionRepository = authSessionRepository;
        this.adminUsernameAuthenticator = adminUsernameAuthenticator;
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
        Principal principal = adminUsernameAuthenticator.authenticate(id, credential)
                .orElseThrow(() -> AuthBusinessException.with(AuthErrors.INVALID_CREDENTIAL));
        AuthSession authSession = AuthSession.create(
                sessionIdGenerator.generate(principal),
                principal,
                Instant.now().plusSeconds(ADMIN_SESSION_TTL)
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
                principal,
                Instant.now().plusSeconds(BRANCH_SESSION_TTL)
        );
        authSessionRepository.save(authSession);
        return authSession;
    }

    public Principal requirePrincipal(SessionId sessionId) {
        AuthSession authSession = authSessionRepository.findById(sessionId).orElse(null);
        if (authSession == null) throw AuthBusinessException.with(AuthErrors.UNAUTHENTICATED);
        if (authSession.isExpired())  throw AuthBusinessException.with(AuthErrors.SESSION_EXPIRED);
        return authSession.getPrincipal();
    }

    public Optional<Principal> findPrincipal(SessionId sessionId) {
        AuthSession authSession = authSessionRepository.findById(sessionId).orElse(null);
        if (authSession == null) return Optional.empty();
        if (authSession.isExpired())  return Optional.empty();
        return Optional.of(authSession.getPrincipal());
    }

    public void authorize(Principal principal, List<String> roles) {
        if (roles.stream().noneMatch(role -> principal.getRole().equals(role))) {
            throw AuthBusinessException.with(AuthErrors.UNAUTHORIZED);
        }
    }

}
