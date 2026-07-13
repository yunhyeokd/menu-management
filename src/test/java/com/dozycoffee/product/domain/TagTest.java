package com.dozycoffee.product.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class TagTest {

    @Test
    public void 태그를_정상_생성한다() {
        Tag tag = TagFixture.builder().build();

        assertThat(tag.getName()).isEqualTo(TagFixture.Defaults.name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@신제품", " 신제품 ", "신 제품",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 태그_생성시_태그명이_유효하지_않으면_예외를_발생시킨다(String tagName) {
        assertThatThrownBy(() -> TagFixture.builder().name(tagName).build())
                .isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 태그_생성시_유효한_태그명은_정상적으로_생성된다(String tagName) {
        assertThatNoException().isThrownBy(() -> TagFixture.builder().name(tagName).build());
    }

    @Test
    public void id가_같은_태그는_동등하다() {
        Tag tag1 = TagFixture.builder().name("신제품1").build();
        Tag tag2 = TagFixture.builder().name("신제품2").build();
        assertThat(tag1).isEqualTo(tag2);
    }

    @Test
    public void id가_다른_태그는_동등하지_않다() {
        Instant createdAt = Instant.now();
        Tag tag1 = TagFixture.builder().name("신제품1").createdAt(createdAt).build();
        Tag tag2 = TagFixture.builder()
                .id(TagId.of("00000000-0000-0000-0000-000000000002"))
                .name("신제품2")
                .createdAt(createdAt)
                .build();
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    public void id가_null인_태그는_동등하지_않다() {
        Tag tag1 = TagFixture.builder().name("신제품1").build();
        Tag tag2 = TagFixture.builder()
                .id(TagId.of("00000000-0000-0000-0000-000000000002"))
                .name("신제품2")
                .build();
        assertThat(tag1).isNotEqualTo(tag2);
    }

}
