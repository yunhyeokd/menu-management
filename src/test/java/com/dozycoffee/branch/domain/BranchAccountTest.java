package com.dozycoffee.branch.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.provider.MethodSource;

public class BranchAccountTest {

    @Test
    public void 지점을_정상_생성한다() {
        BranchCode code = BranchFixture.code;
        String authKeyHash = BranchFixture.authKeyHash;

        BranchAccount branchAccount = BranchAccount.create(BranchId.of(1L), code, authKeyHash);

        assertThat(branchAccount.getCode()).isEqualTo(code);
        assertThat(branchAccount.getAuthKeyHash()).isEqualTo(authKeyHash);
        assertThat(branchAccount.getStatus()).isEqualTo(BranchStatus.ACTIVE);
    }

    private static Stream<String> invalidCodeTestSource() {
        return Stream.of(
                null,
                "", " ", "\n",
                "1".repeat(7), "1".repeat(9),
                "abcdefgh"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCodeTestSource")
    public void 지점_생성시_지점코드가_유효하지_않으면_예외를_발생시킨다(String code) {
        assertThatThrownBy(() -> BranchFixture.builder().code(code).build())
                .isInstanceOf(BranchException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    public void 지점_생성시_인증키가_유효하지_않으면_예외를_발생시킨다(String authKeyHash) {
        assertThatThrownBy(() -> BranchFixture.builder().authKeyHash(authKeyHash).build())
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 지점을_비활성화하면_상태가_INACTIVE로_변경된다() {
        BranchAccount branchAccount = BranchFixture.builder().build();
        branchAccount.deactivate();
        assertThat(branchAccount.getStatus()).isEqualTo(BranchStatus.INACTIVE);
    }

    @Test
    public void 지점을_활성화하면_상태가_ACTIVE로_변경된다() {
        BranchAccount branchAccount = BranchFixture.builder().build();
        branchAccount.deactivate();
        branchAccount.activate();
        assertThat(branchAccount.getStatus()).isEqualTo(BranchStatus.ACTIVE);
    }

    @Test
    public void 지점을_소프트_삭제하면_deletedAt이_설정되고_상태가_INACTIVE로_변경된다() {
        BranchAccount branchAccount = BranchFixture.builder().build();
        assertThat(branchAccount.getDeletedAt()).isNull();
        branchAccount.softDelete();
        assertThat(branchAccount.getDeletedAt()).isNotNull();
        assertThat(branchAccount.getStatus()).isEqualTo(BranchStatus.INACTIVE);
    }

    @Test
    public void 이미_소프트_삭제된_지점을_다시_삭제하면_예외가_발생한다() {
        BranchAccount branchAccount = BranchFixture.builder().build();
        branchAccount.softDelete();
        assertThatThrownBy(branchAccount::softDelete).isInstanceOf(BranchException.class);
    }

    @Test
    public void 같은_id를_가진_지점은_동등하다() {
        Instant now = Instant.now();
        BranchAccount a = BranchAccount.of(BranchId.of(1L), BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null);
        BranchAccount b = BranchAccount.of(BranchId.of(1L), BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_id를_가진_지점은_동등하지_않다() {
        Instant now = Instant.now();
        BranchAccount a = BranchAccount.of(BranchId.of(1L), BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null);
        BranchAccount b = BranchAccount.of(BranchId.of(2L), BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null);
        assertThat(a).isNotEqualTo(b);
    }
}
