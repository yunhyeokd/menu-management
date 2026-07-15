package com.dozycoffee.catalog.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionItemTest {

    @Test
    public void 옵션_아이템을_정상_생성한다() {
        OptionItem optionItem = OptionItemFixture.builder().build();

        assertThat(optionItem.getName()).isEqualTo(OptionItemFixture.Defaults.name);
        assertThat(optionItem.getDescription()).isEqualTo(OptionItemFixture.Defaults.description);
        assertThat(optionItem.getPrice()).isEqualTo(OptionItemFixture.Defaults.price);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@에스프레소 샷", " 에스프레소 샷 ",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 옵션아이템_생성시_옵션아이템명이_유효하지_않으면_예외를_발생시킨다(String name) {
        assertThatThrownBy(() -> OptionItemFixture.builder().name(name).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션아이템_생성시_설명이_최대길이를_초과하면_예외를_발생시킨다() {
        String longDescription = "a".repeat(501);
        assertThatThrownBy(() -> OptionItemFixture.builder().description(longDescription).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 옵션아이템_생성시_가격이_0보다_작으면_예외를_발생시킨다() {
        assertThatThrownBy(() -> OptionItemFixture.builder().price(-1).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 같은_값의_옵션아이템은_동등하다() {
        OptionItem item1 = OptionItemFixture.builder().build();
        OptionItem item2 = OptionItemFixture.builder().build();
        assertThat(item1).isEqualTo(item2);
    }

    @Test
    public void 다른_값의_옵션아이템은_동등하지_않다() {
        OptionItem item1 = OptionItemFixture.builder().build();
        OptionItem item2 = OptionItemFixture.builder().price(OptionItemFixture.Defaults.price + 100).build();
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    public void 동등한_옵션아이템은_해시값이_동일하다() {
        OptionItem item1 = OptionItemFixture.builder().build();
        OptionItem item2 = OptionItemFixture.builder().build();
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }
}
