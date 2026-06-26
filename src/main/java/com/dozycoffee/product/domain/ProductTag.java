package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;

public class ProductTag {

    private ProductId productId;
    private TagId tagId;
    private Instant createdAt;

    private ProductTag(ProductId productId, TagId tagId, Instant createdAt) {
        setProductId(productId);
        setTagId(tagId);
        setCreatedAt(createdAt);
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

    private void setProductId(ProductId productId) {
        if (productId == null) throw new ProductException("productId cannot be null");
        this.productId = productId;
    }

    public TagId getTagId() {
        return tagId;
    }

    private void setTagId(TagId tagId) {
        if (tagId == null) throw new ProductException("tagId cannot be null");
        this.tagId = tagId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.createdAt = createdAt;
    }
}
