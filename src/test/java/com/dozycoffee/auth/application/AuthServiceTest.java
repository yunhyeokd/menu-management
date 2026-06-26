package com.dozycoffee.auth.application;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.auth.domain.AuthSession;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;
import com.dozycoffee.core.domain.Identifier;
import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthServiceTest {

    private FakeAuthSessionRepository sessionRepository;
    private AuthService authService;

    private AdminAccount activeAdmin;
    private BranchAccount activeBranch;
    private BranchAccount deletedBranch;

    private static final String RAW_PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final String BRANCH_CODE = "20240001";
    private static final String RAW_AUTH_KEY = "auth-key";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";

    private final AtomicLong sessionCounter = new AtomicLong(1);

    @BeforeEach
    void setUp() {
        activeAdmin = AdminAccount.create(AdminId.of(1L), AdminRole.SYSTEM, "sysadmin", HASHED_PASSWORD);
        activeBranch = BranchAccount.create(BranchId.of(1L), BranchCode.of(BRANCH_CODE), HASHED_AUTH_KEY);
        deletedBranch = BranchAccount.create(BranchId.of(2L), BranchCode.of("20240002"), HASHED_AUTH_KEY);
        deletedBranch.softDelete();

        sessionRepository = new FakeAuthSessionRepository();

        Authenticator<Identifier<String>> adminAuthenticator = (username, credential) -> {
            if ("sysadmin".equals(username.getValue()) && HASHED_PASSWORD.equals("hashed-" + credential.getValue())) {
                return Optional.of(activeAdmin);
            }
            return Optional.empty();
        };

        Authenticator<BranchCode> branchAuthenticator = (code, credential) -> {
            if (BRANCH_CODE.equals(code.getValue()) && HASHED_AUTH_KEY.equals("hashed-" + credential.getValue())) {
                return Optional.of(activeBranch);
            }
            return Optional.empty();
        };

        SessionIdGenerator sessionIdGenerator = principal ->
                SessionId.of("session-" + sessionCounter.getAndIncrement());

        authService = new AuthService(
                sessionRepository,
                adminAuthenticator,
                branchAuthenticator,
                sessionIdGenerator
        );
    }

    private void assertErrorCode(Throwable e, AuthErrors error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── authenticateAdmin ────────────────────────────────────────────────────

    @Test
    void 관리자_로그인_성공시_세션을_발급한다() {
        AuthSession session = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);

        assertThat(session.getSessionId()).isNotNull();
        assertThat(session.getPrincipal()).isEqualTo(activeAdmin);
        assertThat(sessionRepository.size()).isEqualTo(1);
    }

    @Test
    void 관리자_로그인_실패시_INVALID_CREDENTIAL_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authenticateAdmin("sysadmin", "wrong"))
                .satisfies(e -> assertErrorCode(e, AuthErrors.INVALID_CREDENTIAL));
    }

    // ─── authenticateBranch ───────────────────────────────────────────────────

    @Test
    void 지점_로그인_성공시_세션을_발급한다() {
        AuthSession session = authService.authenticateBranch(BRANCH_CODE, RAW_AUTH_KEY);

        assertThat(session.getSessionId()).isNotNull();
        assertThat(session.getPrincipal()).isEqualTo(activeBranch);
        assertThat(sessionRepository.size()).isEqualTo(1);
    }

    @Test
    void 지점_로그인_실패시_INVALID_CREDENTIAL_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authenticateBranch(BRANCH_CODE, "wrong"))
                .satisfies(e -> assertErrorCode(e, AuthErrors.INVALID_CREDENTIAL));
    }

    // ─── requirePrincipal / findPrincipal ─────────────────────────────────────

    @Test
    void 유효한_세션ID로_Principal을_조회한다() {
        AuthSession session = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);

        Principal principal = authService.requirePrincipal(session.getSessionId());

        assertThat(principal).isEqualTo(activeAdmin);
    }

    @Test
    void 존재하지_않는_세션ID로_조회시_UNAUTHENTICATED_예외가_발생한다() {
        assertThatThrownBy(() -> authService.requirePrincipal(SessionId.of("unknown")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNAUTHENTICATED));
    }

    @Test
    void 만료된_세션으로_requirePrincipal_호출시_SESSION_EXPIRED_예외가_발생한다() {
        AuthSession expiredSession = AuthSession.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expiredSession);

        assertThatThrownBy(() -> authService.requirePrincipal(SessionId.of("expired")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.SESSION_EXPIRED));
    }

    @Test
    void 만료된_세션으로_findPrincipal_호출시_빈_Optional을_반환한다() {
        AuthSession expiredSession = AuthSession.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expiredSession);

        Optional<Principal> result = authService.findPrincipal(SessionId.of("expired"));

        assertThat(result).isEmpty();
    }

    @Test
    void findPrincipal은_세션이_없으면_빈_Optional을_반환한다() {
        Optional<Principal> result = authService.findPrincipal(SessionId.of("unknown"));

        assertThat(result).isEmpty();
    }

    @Test
    void findPrincipal은_세션이_있으면_Principal을_반환한다() {
        AuthSession session = authService.authenticateBranch(BRANCH_CODE, RAW_AUTH_KEY);

        Optional<Principal> result = authService.findPrincipal(session.getSessionId());

        assertThat(result).contains(activeBranch);
    }

    // ─── authorize ────────────────────────────────────────────────────────────

    @Test
    void role이_일치하면_인가에_성공한다() {
        authService.authorize(activeAdmin, List.of(Roles.SYSTEM));
    }

    @Test
    void 여러_role_중_하나라도_일치하면_인가에_성공한다() {
        authService.authorize(activeAdmin, List.of(Roles.STAFF, Roles.SYSTEM));
    }

    @Test
    void role이_일치하지_않으면_UNAUTHORIZED_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authorize(activeAdmin, List.of(Roles.BRANCH)))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNAUTHORIZED));
    }

    // ─── invalidate ───────────────────────────────────────────────────────────

    @Test
    void SessionId로_특정_세션을_무효화한다() {
        AuthSession session1 = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);
        AuthSession session2 = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);

        authService.invalidate(session1.getSessionId());

        assertThat(authService.findPrincipal(session1.getSessionId())).isEmpty();
        assertThat(authService.findPrincipal(session2.getSessionId())).isPresent();
    }

    @Test
    void Principal로_해당_주체의_모든_세션을_무효화한다() {
        AuthSession session1 = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);
        AuthSession session2 = authService.authenticateAdmin("sysadmin", RAW_PASSWORD);
        AuthSession branchSession = authService.authenticateBranch(BRANCH_CODE, RAW_AUTH_KEY);

        authService.invalidate(activeAdmin);

        assertThat(authService.findPrincipal(session1.getSessionId())).isEmpty();
        assertThat(authService.findPrincipal(session2.getSessionId())).isEmpty();
        assertThat(authService.findPrincipal(branchSession.getSessionId())).isPresent();
    }
}
