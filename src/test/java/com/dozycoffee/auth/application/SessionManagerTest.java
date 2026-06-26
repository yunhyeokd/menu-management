package com.dozycoffee.auth.application;

import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.auth.domain.AuthSession;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.auth.domain.SessionId;
import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SessionManagerTest {

    private static final String BRANCH_CODE = "20240001";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";
    private static final String HASHED_PASSWORD = "hashed-password";

    private static final long TTL = 3600L;

    private FakeAuthSessionRepository sessionRepository;
    private SessionManager sessionManager;
    private AdminAccount activeAdmin;
    private BranchAccount activeBranch;

    @BeforeEach
    void setUp() {
        activeAdmin = AdminAccount.create(AdminId.of(1L), AdminRole.SYSTEM, "sysadmin", HASHED_PASSWORD);
        activeBranch = BranchAccount.create(BranchId.of(1L), BranchCode.of(BRANCH_CODE), HASHED_AUTH_KEY);

        sessionRepository = new FakeAuthSessionRepository();
        int[] counter = {1};
        SessionIdGenerator sessionIdGenerator = principal -> SessionId.of("session-" + counter[0]++);
        sessionManager = new SessionManager(sessionRepository, sessionIdGenerator);
    }

    private void assertErrorCode(Throwable e, AuthErrors error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private AuthSession saveSession(Principal principal) {
        return sessionManager.create(principal, TTL);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void 세션을_생성하고_저장한다() {
        AuthSession session = sessionManager.create(activeAdmin, TTL);

        assertThat(session.getSessionId()).isNotNull();
        assertThat(session.getPrincipal()).isEqualTo(activeAdmin);
        assertThat(sessionRepository.size()).isEqualTo(1);
    }

    // ─── requirePrincipal ─────────────────────────────────────────────────────

    @Test
    void 유효한_세션ID로_Principal을_조회한다() {
        AuthSession session = saveSession(activeAdmin);

        Principal principal = sessionManager.requirePrincipal(session.getSessionId());

        assertThat(principal).isEqualTo(activeAdmin);
    }

    @Test
    void 존재하지_않는_세션ID로_조회시_UNAUTHENTICATED_예외가_발생한다() {
        assertThatThrownBy(() -> sessionManager.requirePrincipal(SessionId.of("unknown")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNAUTHENTICATED));
    }

    @Test
    void 만료된_세션으로_requirePrincipal_호출시_SESSION_EXPIRED_예외가_발생한다() {
        AuthSession expiredSession = AuthSession.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expiredSession);

        assertThatThrownBy(() -> sessionManager.requirePrincipal(SessionId.of("expired")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.SESSION_EXPIRED));
    }

    // ─── findPrincipal ────────────────────────────────────────────────────────

    @Test
    void 유효한_세션ID로_Principal을_Optional로_반환한다() {
        AuthSession session = saveSession(activeBranch);

        Optional<Principal> result = sessionManager.findPrincipal(session.getSessionId());

        assertThat(result).contains(activeBranch);
    }

    @Test
    void 세션이_없으면_빈_Optional을_반환한다() {
        assertThat(sessionManager.findPrincipal(SessionId.of("unknown"))).isEmpty();
    }

    @Test
    void 만료된_세션으로_findPrincipal_호출시_빈_Optional을_반환한다() {
        AuthSession expiredSession = AuthSession.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expiredSession);

        assertThat(sessionManager.findPrincipal(SessionId.of("expired"))).isEmpty();
    }

    // ─── invalidate ───────────────────────────────────────────────────────────

    @Test
    void SessionId로_특정_세션을_무효화한다() {
        AuthSession session1 = saveSession(activeAdmin);
        AuthSession session2 = AuthSession.create(SessionId.of("session2"), activeAdmin, Instant.now().plusSeconds(3600));
        sessionRepository.save(session2);

        sessionManager.invalidate(session1.getSessionId());

        assertThat(sessionManager.findPrincipal(session1.getSessionId())).isEmpty();
        assertThat(sessionManager.findPrincipal(session2.getSessionId())).isPresent();
    }

    @Test
    void Principal로_해당_주체의_모든_세션을_무효화한다() {
        AuthSession session1 = saveSession(activeAdmin);
        AuthSession session2 = AuthSession.create(SessionId.of("session2"), activeAdmin, Instant.now().plusSeconds(3600));
        sessionRepository.save(session2);
        AuthSession branchSession = saveSession(activeBranch);

        sessionManager.invalidate(activeAdmin);

        assertThat(sessionManager.findPrincipal(session1.getSessionId())).isEmpty();
        assertThat(sessionManager.findPrincipal(session2.getSessionId())).isEmpty();
        assertThat(sessionManager.findPrincipal(branchSession.getSessionId())).isPresent();
    }
}
