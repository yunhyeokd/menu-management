package com.dozycoffee.domain.product;

import com.dozycoffee.domain.product.ProductException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class TagTest {

    @Test
    public void 태그를_정상_생성한다() {
        String name = "신제품";

        Tag tag = Tag.create(name);

        assertThat(tag.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@신제품", " 신제품 ", "신 제품",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 태그_생성시_태그명이_유효하지_않으면_예외를_발생시킨다(String tagName) {
        assertThatThrownBy(() -> Tag.create(tagName))
                .isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 태그_생성시_유효한_태그명은_정상적으로_생성된다(String tagName) {
        assertThatNoException().isThrownBy(() -> Tag.create(tagName));
    }

    @Test
    public void id가_같은_태그는_동등하다() {
        Tag tag1 = Tag.of(1L, "신제품1", Instant.now());
        Tag tag2 = Tag.of(1L, "신제품2", Instant.now());
        assertThat(tag1).isEqualTo(tag2);
    }

    @Test
    public void id가_다른_태그는_동등하지_않다() {
        Instant createdAt = Instant.now();
        Tag tag1 = Tag.of(1L, "신제품1", createdAt);
        Tag tag2 = Tag.of(2L, "신제품2", createdAt);
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    public void id가_null인_태그는_동등하지_않다() {
        Tag tag1 = Tag.of(1L, "신제품1", Instant.now());
        Tag tag2 = Tag.create("신제품2");
        assertThat(tag1).isNotEqualTo(tag2);
    }

}
