package com.dozycoffee.product.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTagTest {

    @Test
    public void 상품태그를_정상_생성한다() {
        ProductId productId = ProductId.of(1L);
        TagId tagId = TagId.of(1L);

        ProductTag productTag = ProductTag.create(productId, tagId);

        assertThat(productId).isEqualTo(productTag.getProductId());
        assertThat(tagId).isEqualTo(productTag.getTagId());
        assertThat(productTag.getCreatedAt()).isNotNull();
    }

    @Test
    public void 복합키가_같은_상품태그는_동등하다() {
        ProductId productId = ProductId.of(1L);
        TagId tagId = TagId.of(1L);
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(productId, tagId, createdAt);
        ProductTag productTag2 = ProductTag.of(productId, tagId, createdAt);

        assertThat(productTag1).isEqualTo(productTag2);
    }

    @Test
    public void tagId가_다른_상품태그는_동등하지_않다() {
        ProductId productId = ProductId.of(1L);
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(productId, TagId.of(1L), createdAt);
        ProductTag productTag2 = ProductTag.of(productId, TagId.of(2L), createdAt);

        assertThat(productTag1).isNotEqualTo(productTag2);
    }

}
