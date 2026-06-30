package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductTag;
import com.dozycoffee.product.domain.TagId;

import java.time.Instant;

public record ProductTagRow(
        String productId,
        String tagId,
        Instant createdAt
) {
    public ProductTag toProductTag() {
        return ProductTag.of(ProductId.of(productId), TagId.of(tagId), createdAt);
    }
}
