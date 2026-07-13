package com.dozycoffee.branch.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchTest {

    @Test
    public void 지점을_정상_생성한다() {
        Branch branch = BranchFixture.builder().build();

        assertThat(branch.getCode()).isEqualTo(BranchFixture.code);
        assertThat(branch.getAuthKeyHash()).isEqualTo(BranchFixture.authKeyHash);
        assertThat(branch.getStatus()).isEqualTo(BranchStatus.ACTIVE);
        assertThat(branch.getName()).isEqualTo(BranchFixture.name);
        assertThat(branch.getAddress()).isEqualTo(BranchFixture.address);
    }

    private static Stream<String> invalidCodeTestSource() {
        return Stream.of(
                null, "", " ", "\n",
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

    private static Stream<String> invalidNameTestSource() {
        return Stream.of(
                null, "", " ", "\n",
                " abc", "abc ", " abc ",
                "abc@", "-()&. ",
                "a".repeat(31)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameTestSource")
    public void 지점_생성시_지점명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> BranchFixture.builder().name(name).build())
                .isInstanceOf(BranchException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"St.오늘&내일점(어제 - 모레)"})
    public void 유효한_지점명이면_정상_생성된다(String name) {
        Branch branch = BranchFixture.builder().name(name).build();
        assertThat(branch.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", " aaa "})
    public void 지점_생성시_주소가_유효하지_않으면_예외를_발생시킨다(String address) {
        assertThatThrownBy(() -> BranchFixture.builder().address(address).build())
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 주소가_255자를_초과하면_예외가_발생한다() {
        assertThatThrownBy(() -> BranchFixture.builder().address("a".repeat(256)).build())
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 지점을_비활성화하면_상태가_INACTIVE로_변경된다() {
        Branch branch = BranchFixture.builder().build();
        branch.deactivate();
        assertThat(branch.getStatus()).isEqualTo(BranchStatus.INACTIVE);
    }

    @Test
    public void 지점을_활성화하면_상태가_ACTIVE로_변경된다() {
        Branch branch = BranchFixture.builder().build();
        branch.deactivate();
        branch.activate();
        assertThat(branch.getStatus()).isEqualTo(BranchStatus.ACTIVE);
    }

    @Test
    public void 지점을_소프트_삭제하면_deletedAt이_설정되고_상태가_INACTIVE로_변경된다() {
        Branch branch = BranchFixture.builder().build();
        assertThat(branch.getDeletedAt()).isNull();
        branch.softDelete();
        assertThat(branch.getDeletedAt()).isNotNull();
        assertThat(branch.getStatus()).isEqualTo(BranchStatus.INACTIVE);
    }

    @Test
    public void 이미_소프트_삭제된_지점을_다시_삭제하면_예외가_발생한다() {
        Branch branch = BranchFixture.builder().build();
        branch.softDelete();
        assertThatThrownBy(branch::softDelete).isInstanceOf(BranchException.class);
    }

    @Test
    public void 같은_id를_가진_지점은_동등하다() {
        Instant now = Instant.now();
        Branch a = Branch.of(BranchFixture.id, BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null, BranchFixture.name, BranchFixture.address);
        Branch b = Branch.of(BranchFixture.id, BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null, BranchFixture.name, BranchFixture.address);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_id를_가진_지점은_동등하지_않다() {
        Instant now = Instant.now();
        Branch a = Branch.of(BranchFixture.id, BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null, BranchFixture.name, BranchFixture.address);
        Branch b = Branch.of(BranchId.of("00000000-0000-0000-0000-000000000002"), BranchFixture.code, BranchFixture.authKeyHash, BranchStatus.ACTIVE, now, null, BranchFixture.name, BranchFixture.address);
        assertThat(a).isNotEqualTo(b);
    }
}
