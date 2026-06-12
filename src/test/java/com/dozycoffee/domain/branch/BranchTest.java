package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.common.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchTest {

    @Test
    public void 지점을_정상_생성한다() {
        String code = BranchFixture.code;
        String name = BranchFixture.name;
        String address = BranchFixture.address;
        String authKeyHash = BranchFixture.authKeyHash;

        Branch branch = Branch.create(code, name, address, authKeyHash);

        assertThat(branch.getCode()).isEqualTo(code);
        assertThat(branch.getName()).isEqualTo(name);
        assertThat(branch.getAddress()).isEqualTo(address);
        assertThat(branch.getAuthKeyHash()).isEqualTo(authKeyHash);
        assertThat(branch.getStatus()).isEqualTo(BranchStatus.ACTIVE);
    }

    private static Stream<String> invalidCodeTestSource() {
        return Stream.of(
                null,
                // 빈 문자열
                "", " ", "\n",
                // 8자 미만/초과
                "1".repeat(7), "1".repeat(9),
                // 숫자 외의 문자
                "abcdefgh"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCodeTestSource")
    public void 지점_생성시_지점코드가_유효하지_않으면_예외를_발생시킨다(String code) {
        assertThatThrownBy(() -> BranchFixture.builder().code(code).build())
                .isInstanceOf(DomainException.class);
    }

    private static Stream<String> invalidNameTestSource() {
        return Stream.of(
                null,
                // 빈 문자열
                "", " ", "\n",
                // 앞뒤 공백 존재
                " abc", "abc ", " abc ",
                // 허용 외 특수문자
                "abc@",
                // 허용된 특수문자로만 구성
                "-()&. ",
                // 30자 초과
                "a".repeat(31)
        );
    }

    @ParameterizedTest
    @MethodSource(value = "invalidNameTestSource")
    public void 지점_생성시_지점명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> BranchFixture.builder().name(name).build())
                .isInstanceOf(DomainException.class);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "St.오늘&내일점(어제 - 모레)"
    })
    public void 지점_생성시_유효한_지점명이면_정상_생성된다(String name) {
        Branch branch = BranchFixture.builder().name(name).build();
        assertThat(branch.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", " aaa "})
    public void 지점_생성시_주소가_유효하지_않으면_예외를_발생시킨다(String address) {
        assertThatThrownBy(() -> BranchFixture.builder().address(address).build())
                .isInstanceOf(DomainException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    public void 지점_생성시_인증키가_유효하지_않으면_예외를_발생시킨다(String authKeyHash) {
        assertThatThrownBy(() -> BranchFixture.builder().authKeyHash(authKeyHash).build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    public void 같은_id를_가진_지점은_동등하다() {
        String code = BranchFixture.code;
        String name = BranchFixture.name;
        String address = BranchFixture.address;
        String authKeyHash = BranchFixture.authKeyHash;
        BranchStatus status = BranchStatus.ACTIVE;
        Instant now = Instant.now();
        Branch branch1 = Branch.of(1L, code, name, address, authKeyHash, status, now, null);
        Branch branch2 = Branch.of(1L, code, name, address, authKeyHash, status, now, null);
        assertThat(branch1).isEqualTo(branch2);
        assertThat(branch1.hashCode()).isEqualTo(branch2.hashCode());
    }

    @Test
    public void 다른_id를_가진_지점은_동등하지_않다() {
        String code = BranchFixture.code;
        String name = BranchFixture.name;
        String address = BranchFixture.address;
        String authKeyHash = BranchFixture.authKeyHash;
        BranchStatus status = BranchStatus.ACTIVE;
        Instant now = Instant.now();
        Branch branch1 = Branch.of(1L, code, name, address, authKeyHash, status, now, null);
        Branch branch2 = Branch.of(2L, code, name, address, authKeyHash, status, now, null);
        assertThat(branch1).isNotEqualTo(branch2);
    }

    @Test
    public void id가_null인_지점은_동등하지_않다() {
        String code = BranchFixture.code;
        String name = BranchFixture.name;
        String address = BranchFixture.address;
        String authKeyHash = BranchFixture.authKeyHash;
        BranchStatus status = BranchStatus.ACTIVE;
        Instant now = Instant.now();
        Branch branch1 = Branch.of(1L, code, name, address, authKeyHash, status, now, null);
        Branch branch2 = Branch.create(code, name, address, authKeyHash);assertThat(branch1).isNotEqualTo(branch2);
    }


}
