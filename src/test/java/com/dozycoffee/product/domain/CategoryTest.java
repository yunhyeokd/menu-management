package com.dozycoffee.product.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryTest {

    @Test
    public void 카테고리를_정상_생성한다() {
        Category category = CategoryFixture.builder().build();

        assertThat(category.getName()).isEqualTo(CategoryFixture.Defaults.name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "@커피", " 커피 ",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 카테고리_생성시_카테고리명이_유효하지_않으면_예외를_발생시킨다(String categoryName) {
        assertThatThrownBy(() -> CategoryFixture.builder().name(categoryName).build())
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_카테고리는_동등하다() {
        Category category1 = CategoryFixture.builder().name("커피1").build();
        Category category2 = CategoryFixture.builder().name("커피2").build();
        assertThat(category1).isEqualTo(category2);
    }

    @Test
    public void id가_다른_카테고리는_동등하지_않다() {
        Instant createdAt = Instant.now();
        Category category1 = CategoryFixture.builder().name("커피1").createdAt(createdAt).build();
        Category category2 = CategoryFixture.builder()
                .id(CategoryId.of("00000000-0000-0000-0000-000000000002"))
                .name("커피1")
                .createdAt(createdAt)
                .build();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    public void id가_null인_카테고리는_동등하지_않다() {
        Category category1 = CategoryFixture.builder().name("커피1").build();
        Category category2 = CategoryFixture.builder()
                .id(CategoryId.of("00000000-0000-0000-0000-000000000002"))
                .name("커피1")
                .build();
        assertThat(category1).isNotEqualTo(category2);
    }

}
