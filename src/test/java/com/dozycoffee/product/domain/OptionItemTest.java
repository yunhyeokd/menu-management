package com.dozycoffee.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionItemTest {

    OptionGroupId validOptionGroupId = OptionGroupId.of(1L);
    String validName = "에스프레소 샷";
    String validDescription = "음료에 추가할 에스프레소 샷 수량을 선택할 수 있습니다";
    int validPrice = 100;

    @Test
    public void 옵션_아이템을_정상_생성한다() {

        OptionItem optionItem = OptionItem.create(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, validPrice);
        assertThat(optionItem.getOptionGroupId()).isEqualTo(validOptionGroupId);
        assertThat(optionItem.getName()).isEqualTo(validName);
        assertThat(optionItem.getDescription()).isEqualTo(validDescription);
        assertThat(optionItem.getPrice()).isEqualTo(validPrice);
    }


    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@에스프레소 샷", " 에스프레소 샷 ",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 옵션아이템_생성시_옵션아이템명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> OptionItem.create(OptionItemId.of(1L), validOptionGroupId, name, validDescription, validPrice))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션아이템_생성시_설명이_최대길이를_초과하면_예외를_발생시킨다() {
        String longDescription = "a".repeat(501);
        assertThatThrownBy(() -> OptionItem.create(OptionItemId.of(1L), validOptionGroupId, validName, longDescription, validPrice))
                .isInstanceOf(ProductException.class);
    }


    @Test
    public void 옵션아이템_생성시_가격이_0보다_작으면_예외를_발생시킨다() {
        int invalidPrice = -1;
        assertThatThrownBy(() -> OptionItem.create(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, invalidPrice))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_옵션아이템은_동등하다() {
        OptionItem optionItem1 = OptionItem.of(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, validPrice, Instant.now());
        OptionItem optionItem2 = OptionItem.of(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, validPrice, Instant.now());
        assertThat(optionItem1).isEqualTo(optionItem2);
    }

    @Test
    public void id가_다른_옵션아이템은_동등하지_않다() {
        Instant createdAt = Instant.now();
        OptionItem optionItem1 = OptionItem.of(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, validPrice, createdAt);
        OptionItem optionItem2 = OptionItem.of(OptionItemId.of(2L), validOptionGroupId, validName, validDescription, validPrice, createdAt);
        assertThat(optionItem1).isNotEqualTo(optionItem2);
    }

    @Test
    public void id가_null인_옵션아이템은_동등하지_않다() {
        OptionItem optionItem1 = OptionItem.of(OptionItemId.of(1L), validOptionGroupId, validName, validDescription, validPrice, Instant.now());
        OptionItem optionItem2 = OptionItem.create(OptionItemId.of(2L), validOptionGroupId, validName, validDescription, validPrice);
        assertThat(optionItem1).isNotEqualTo(optionItem2);
    }


}
