package com.dozycoffee.core.session;

import com.dozycoffee.core.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionTest {

    private static final SessionId SESSION_ID = SessionId.of("test-session");
    private static final String CONTEXT = "context";

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void 세션을_생성한다() {
        Instant expiresAt = Instant.now().plusSeconds(3600);
        Session<String> session = Session.create(SESSION_ID, CONTEXT, expiresAt);

        assertThat(session.getSessionId()).isEqualTo(SESSION_ID);
        assertThat(session.getContext()).isEqualTo(CONTEXT);
        assertThat(session.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void sessionId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Session.create(null, CONTEXT, Instant.now().plusSeconds(3600)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void context가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Session.create(SESSION_ID, null, Instant.now().plusSeconds(3600)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void expiresAt이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Session.create(SESSION_ID, CONTEXT, null))
                .isInstanceOf(DomainException.class);
    }

    // ─── isExpired ────────────────────────────────────────────────────────────

    @Test
    void 만료_시각이_지나지_않은_세션은_만료되지_않았다() {
        Session<String> session = Session.create(SESSION_ID, CONTEXT, Instant.now().plusSeconds(3600));

        assertThat(session.isExpired()).isFalse();
    }

    @Test
    void 만료_시각이_지난_세션은_만료됐다() {
        Session<String> session = Session.create(SESSION_ID, CONTEXT, Instant.now().minusSeconds(1));

        assertThat(session.isExpired()).isTrue();
    }

    // ─── extendExpiry ─────────────────────────────────────────────────────────

    @Test
    void 만료_시각을_연장한다() {
        Session<String> session = Session.create(SESSION_ID, CONTEXT, Instant.now().plusSeconds(60));
        Instant extended = Instant.now().plusSeconds(7200);

        session.extendExpiry(extended);

        assertThat(session.getExpiresAt()).isEqualTo(extended);
    }

    @Test
    void extendExpiry에_null을_전달하면_예외가_발생한다() {
        Session<String> session = Session.create(SESSION_ID, CONTEXT, Instant.now().plusSeconds(3600));

        assertThatThrownBy(() -> session.extendExpiry(null))
                .isInstanceOf(DomainException.class);
    }
}
