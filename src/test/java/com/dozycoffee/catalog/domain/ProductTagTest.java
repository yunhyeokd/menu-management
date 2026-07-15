package com.dozycoffee.catalog.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTagTest {

    @Test
    public void 상품태그를_정상_생성한다() {
        ProductId productId = ProductFixture.Defaults.id;
        TagId tagId = TagFixture.Defaults.id;

        ProductTag productTag = ProductTag.create(productId, tagId);

        assertThat(productId).isEqualTo(productTag.getProductId());
        assertThat(tagId).isEqualTo(productTag.getTagId());
        assertThat(productTag.getCreatedAt()).isNotNull();
    }

    @Test
    public void 복합키가_같은_상품태그는_동등하다() {
        ProductId productId = ProductFixture.Defaults.id;
        TagId tagId = TagFixture.Defaults.id;
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(productId, tagId, createdAt);
        ProductTag productTag2 = ProductTag.of(productId, tagId, createdAt);

        assertThat(productTag1).isEqualTo(productTag2);
    }

    @Test
    public void tagId가_다른_상품태그는_동등하지_않다() {
        ProductId productId = ProductFixture.Defaults.id;
        Instant createdAt = Instant.now();

        ProductTag productTag1 = ProductTag.of(productId, TagFixture.Defaults.id, createdAt);
        ProductTag productTag2 = ProductTag.of(productId, TagId.of("00000000-0000-0000-0000-000000000002"), createdAt);

        assertThat(productTag1).isNotEqualTo(productTag2);
    }

}
