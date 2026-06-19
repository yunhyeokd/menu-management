package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;

public class ProductTag {

    private Long id;
    private ProductId productId;
    private TagId tagId;
    private Instant createdAt;

    private ProductTag(Long id, ProductId productId, TagId tagId, Instant createdAt) {
        setProductId(productId);
        setTagId(tagId);
        setCreatedAt(createdAt);
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProductTag productTag)) return false;
        if (id == null || productTag.id == null) return false;
        return Objects.equals(id, productTag.id);
    }

    public static ProductTag of(long id, ProductId productId, TagId tagId, Instant createdAt) {
        return new ProductTag(id, productId, tagId, createdAt);
    }

    public static ProductTag create(ProductId productId, TagId tagId) {
        return new ProductTag(null, productId, tagId, Instant.now());
    }

    public Long getId() {
        return id;
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
