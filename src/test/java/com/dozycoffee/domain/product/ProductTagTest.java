package com.dozycoffee.domain.product;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTagTest {

    @Test
    public void 상품태그를_정상_생성한다() {
        long productId = 1L;
        long tagId = 1L;

        ProductTag productTag = ProductTag.create(productId, tagId);

        assertThat(productId).isEqualTo(productTag.getProductId());
        assertThat(tagId).isEqualTo(productTag.getTagId());
    }

    @Test
    public void id가_같은_상품태그는_동등하다() {

        long productId = 1L;
        long tagId = 1L;
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(1L, productId, tagId, createdAt);
        ProductTag productTag2 = ProductTag.of(1L, productId, tagId, createdAt);

        assertThat(productTag1.getId()).isEqualTo(productTag2.getId());
        assertThat(productTag1).isEqualTo(productTag2);
    }


    @Test
    public void id가_다른_상품태그는_동등하지_않다() {

        long productId = 1L;
        long tagId = 1L;
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(1L, productId, tagId, createdAt);
        ProductTag productTag2 = ProductTag.of(2L, productId, tagId, createdAt);

        assertThat(productTag1.getId()).isNotEqualTo(productTag2.getId());
        assertThat(productTag1).isNotEqualTo(productTag2);
    }

    @Test
    public void id가_null인_상품태그는_동등하지_않다() {

        long productId = 1L;
        long tagId = 1L;
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(1L, productId, tagId, createdAt);
        ProductTag productTag2 = ProductTag.create(productId, tagId);

        assertThat(productTag1.getId()).isNotEqualTo(productTag2.getId());
        assertThat(productTag1).isNotEqualTo(productTag2);
    }



}
