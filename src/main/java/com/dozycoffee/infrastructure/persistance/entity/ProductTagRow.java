package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductTag;
import com.dozycoffee.catalog.domain.TagId;

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
