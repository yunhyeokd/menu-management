package com.dozycoffee.auth.application;

import com.dozycoffee.admin.domain.AdminFixture;
import com.dozycoffee.admin.domain.SystemAdmin;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchFixture;
import com.dozycoffee.core.exception.service.ServiceException;
import com.dozycoffee.core.session.Session;
import com.dozycoffee.core.session.SessionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthSessionManagerTest {

    private static final String BRANCH_CODE = "20240001";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final long TTL = 3600L;

    private FakeAuthSessionRepository sessionRepository;
    private AuthSessionManager sessionManager;
    private SystemAdmin activeAdmin;
    private Branch activeBranch;

    @BeforeEach
    void setUp() {
        activeAdmin = AdminFixture.system().username("sysadmin").password(HASHED_PASSWORD).build();
        activeBranch = BranchFixture.builder().code(BRANCH_CODE).authKeyHash(HASHED_AUTH_KEY).name("테스트점").address("서울 강남구 테헤란로 1").build();

        sessionRepository = new FakeAuthSessionRepository();
        AtomicInteger counter = new AtomicInteger(1);
        sessionManager = new AuthSessionManager(sessionRepository, () -> SessionId.of("session-" + counter.getAndIncrement()));
    }

    private void assertErrorCode(Throwable e, AuthErrors error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Session<Principal> saveSession(Principal principal) {
        return sessionManager.create(principal, TTL);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void 세션을_생성하고_저장한다() {
        Session<Principal> session = sessionManager.create(activeAdmin, TTL);

        assertThat(session.getSessionId()).isNotNull();
        assertThat(session.getContext()).isEqualTo(activeAdmin);
        assertThat(sessionRepository.size()).isEqualTo(1);
    }

    // ─── find ─────────────────────────────────────────────────────────────────

    @Test
    void 유효한_세션ID로_세션을_조회한다() {
        Session<Principal> session = saveSession(activeAdmin);

        Optional<Session<Principal>> result = sessionManager.find(session.getSessionId());

        assertThat(result).isPresent();
        assertThat(result.get().getContext()).isEqualTo(activeAdmin);
    }

    @Test
    void 존재하지_않는_세션ID로_조회시_빈_Optional을_반환한다() {
        assertThat(sessionManager.find(SessionId.of("unknown"))).isEmpty();
    }

    @Test
    void 만료된_세션은_find시_빈_Optional을_반환한다() {
        Session<Principal> expired = Session.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expired);

        assertThat(sessionManager.find(SessionId.of("expired"))).isEmpty();
    }

    // ─── requirePrincipal / findPrincipal ─────────────────────────────────────

    @Test
    void 유효한_세션ID로_Principal을_조회한다() {
        Session<Principal> session = saveSession(activeAdmin);

        Principal principal = sessionManager.requirePrincipal(session.getSessionId());

        assertThat(principal).isEqualTo(activeAdmin);
    }

    @Test
    void 존재하지_않는_세션ID로_requirePrincipal_호출시_UNAUTHENTICATED_예외가_발생한다() {
        assertThatThrownBy(() -> sessionManager.requirePrincipal(SessionId.of("unknown")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNAUTHENTICATED));
    }

    @Test
    void 만료된_세션으로_requirePrincipal_호출시_SESSION_EXPIRED_예외가_발생한다() {
        Session<Principal> expired = Session.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expired);

        assertThatThrownBy(() -> sessionManager.requirePrincipal(SessionId.of("expired")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.SESSION_EXPIRED));
    }

    @Test
    void findPrincipal은_유효한_세션이면_Principal을_반환한다() {
        Session<Principal> session = saveSession(activeBranch);

        assertThat(sessionManager.findPrincipal(session.getSessionId())).contains(activeBranch);
    }

    @Test
    void findPrincipal은_세션이_없으면_빈_Optional을_반환한다() {
        assertThat(sessionManager.findPrincipal(SessionId.of("unknown"))).isEmpty();
    }

    @Test
    void findPrincipal은_만료된_세션이면_빈_Optional을_반환한다() {
        Session<Principal> expired = Session.of(SessionId.of("expired"), activeAdmin, Instant.now().minusSeconds(1));
        sessionRepository.save(expired);

        assertThat(sessionManager.findPrincipal(SessionId.of("expired"))).isEmpty();
    }

    // ─── invalidate ───────────────────────────────────────────────────────────

    @Test
    void SessionId로_특정_세션을_무효화한다() {
        Session<Principal> session1 = saveSession(activeAdmin);
        Session<Principal> session2 = saveSession(activeAdmin);

        sessionManager.invalidate(session1.getSessionId());

        assertThat(sessionManager.findPrincipal(session1.getSessionId())).isEmpty();
        assertThat(sessionManager.findPrincipal(session2.getSessionId())).isPresent();
    }
}