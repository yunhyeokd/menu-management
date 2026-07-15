package com.dozycoffee.catalog.domain;

import java.time.Instant;
import java.util.Objects;

public class ProductTag {

    private final ProductId productId;
    private final TagId tagId;
    private final Instant createdAt;

    private ProductTag(ProductId productId, TagId tagId, Instant createdAt) {
        if (productId == null) throw new ProductException("Product id cannot be null");
        if (tagId == null) throw new ProductException("Tag id cannot be null");
        if (createdAt == null) throw new ProductException("CreatedAt cannot be null");
        this.productId = productId;
        this.tagId = tagId;
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, tagId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProductTag productTag)) return false;
        return Objects.equals(productId, productTag.productId) && Objects.equals(tagId, productTag.tagId);
    }

    public static ProductTag of(ProductId productId, TagId tagId, Instant createdAt) {
        return new ProductTag(productId, tagId, createdAt);
    }

    public static ProductTag create(ProductId productId, TagId tagId) {
        return new ProductTag(productId, tagId, Instant.now());
    }

    public ProductId getProductId() {
        return productId;
    }
    public TagId getTagId() {
        return tagId;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
}
