package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;

public class ProductTag {

    private Long id;
    private long productId;
    private long tagId;
    private Instant createdAt;

    private ProductTag(Long id, long productId, long tagId, Instant createdAt) {
        validateRequiredFields(createdAt);
        this.id = id;
        this.productId = productId;
        this.tagId = tagId;
        this.createdAt = createdAt;
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

    public static ProductTag of(long id, long productId, long tagId, Instant createdAt) {
        return new ProductTag(id, productId, tagId, createdAt);
    }

    public static ProductTag create(long productId, long tagId) {
        return new ProductTag(null, productId, tagId, Instant.now());
    }

    private void validateRequiredFields(Instant createdAt) {
        if (createdAt == null) {
            throw new ProductException("ProductTag createdAt must not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public long getTagId() {
        return tagId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
