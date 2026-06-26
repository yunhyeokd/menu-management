package com.dozycoffee.branch.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.provider.MethodSource;

public class BranchProfileTest {

    @Test
    public void 지점_프로필을_정상_생성한다() {
        BranchProfile profile = BranchProfileFixture.builder().build();

        assertThat(profile.getName()).isEqualTo(BranchProfileFixture.name);
        assertThat(profile.getAddress()).isEqualTo(BranchProfileFixture.address);
        assertThat(profile.getBranchId()).isEqualTo(BranchProfileFixture.branchId);
    }

    private static Stream<String> invalidNameTestSource() {
        return Stream.of(
                null,
                "", " ", "\n",
                " abc", "abc ", " abc ",
                "abc@",
                "-()&. ",
                "a".repeat(31)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameTestSource")
    public void 지점명이_유효하지_않으면_예외가_발생한다(String name) {
        assertThatThrownBy(() -> BranchProfileFixture.builder().name(name).build())
                .isInstanceOf(BranchException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "St.오늘&내일점(어제 - 모레)"
    })
    public void 유효한_지점명이면_정상_생성된다(String name) {
        BranchProfile profile = BranchProfileFixture.builder().name(name).build();
        assertThat(profile.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", " aaa "})
    public void 주소가_유효하지_않으면_예외가_발생한다(String address) {
        assertThatThrownBy(() -> BranchProfileFixture.builder().address(address).build())
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 주소가_255자를_초과하면_예외가_발생한다() {
        String address = "a".repeat(256);
        assertThatThrownBy(() -> BranchProfileFixture.builder().address(address).build())
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 같은_branchId를_가진_프로필은_동등하다() {
        BranchProfile a = BranchProfile.of(BranchId.of(1L), BranchProfileFixture.name, BranchProfileFixture.address);
        BranchProfile b = BranchProfile.of(BranchId.of(1L), BranchProfileFixture.name, BranchProfileFixture.address);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_branchId를_가진_프로필은_동등하지_않다() {
        BranchProfile a = BranchProfile.of(BranchId.of(1L), BranchProfileFixture.name, BranchProfileFixture.address);
        BranchProfile b = BranchProfile.of(BranchId.of(2L), BranchProfileFixture.name, BranchProfileFixture.address);
        assertThat(a).isNotEqualTo(b);
    }
}
