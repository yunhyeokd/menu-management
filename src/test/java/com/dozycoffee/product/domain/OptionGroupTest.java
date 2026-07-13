package com.dozycoffee.product.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionGroupTest {

    @Test
    public void 옵션_그룹을_정상_생성한다() {
        OptionGroup optionGroup = OptionGroupFixture.builder().build();

        assertThat(optionGroup.getName()).isEqualTo(OptionGroupFixture.Defaults.name);
        assertThat(optionGroup.getDescription()).isEqualTo(OptionGroupFixture.Defaults.description);
        assertThat(optionGroup.getItems()).hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@에스프레소 샷", " 에스프레소 샷 ",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 옵션그룹_생성시_옵션그룹명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> OptionGroupFixture.builder().name(name).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션그룹_생성시_설명이_최대길이를_초과하면_예외를_발생시킨다() {
        String longDescription = "a".repeat(501);
        assertThatThrownBy(() -> OptionGroupFixture.builder().description(longDescription).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션그룹_생성시_아이템이_없으면_예외를_발생시킨다() {
        assertThatThrownBy(() -> OptionGroupFixture.builder().items(List.of()).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_옵션그룹은_동등하다() {
        OptionGroup optionGroup1 = OptionGroupFixture.builder().build();
        OptionGroup optionGroup2 = OptionGroupFixture.builder().build();
        assertThat(optionGroup1).isEqualTo(optionGroup2);
    }

    @Test
    public void id가_다른_옵션그룹은_동등하지_않다() {
        Instant createdAt = Instant.now();
        OptionGroup optionGroup1 = OptionGroupFixture.builder().createdAt(createdAt).build();
        OptionGroup optionGroup2 = OptionGroupFixture.builder()
                .id(OptionGroupId.of("00000000-0000-0000-0000-000000000002"))
                .createdAt(createdAt)
                .build();
        assertThat(optionGroup1).isNotEqualTo(optionGroup2);
    }
}
