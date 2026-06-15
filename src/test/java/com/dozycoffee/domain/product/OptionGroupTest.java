package com.dozycoffee.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionGroupTest {

    String validName = "에스프레소 샷";
    String validDescription = "음료에 추가할 에스프레소 샷 수량을 선택할 수 있습니다";

    @Test
    public void 옵션_그룹을_정상_생성한다() {

        OptionGroup optionGroup = OptionGroup.create(validName, validDescription);
        assertThat(optionGroup.getName()).isEqualTo(validName);
        assertThat(optionGroup.getDescription()).isEqualTo(validDescription);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@에스프레소 샷", " 에스프레소 샷 ",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 옵션그룹_생성시_옵션그룹명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> OptionGroup.create(name, validDescription))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션그룹_생성시_설명이_최대길이를_초과하면_예외를_발생시킨다() {
        String longDescription = "a".repeat(501);
        assertThatThrownBy(() -> OptionGroup.create(validName, longDescription))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_옵션그룹은_동등하다() {
        OptionGroup optionGroup1 = OptionGroup.of(1L, validName, validDescription, Instant.now());
        OptionGroup optionGroup2 = OptionGroup.of(1L, validName, validDescription, Instant.now());
        assertThat(optionGroup1).isEqualTo(optionGroup2);
    }

    @Test
    public void id가_다른_옵션그룹은_동등하지_않다() {
        Instant createdAt = Instant.now();
        OptionGroup optionGroup1 = OptionGroup.of(1L, validName, validDescription, createdAt);
        OptionGroup optionGroup2 = OptionGroup.of(2L, validName, validDescription, createdAt);
        assertThat(optionGroup1).isNotEqualTo(optionGroup2);
    }

    @Test
    public void id가_null인_옵션그룹은_동등하지_않다() {
        OptionGroup optionGroup1 = OptionGroup.of(1L, validName, validDescription, Instant.now());
        OptionGroup optionGroup2 = OptionGroup.create(validName, validDescription);
        assertThat(optionGroup1).isNotEqualTo(optionGroup2);
    }

}
