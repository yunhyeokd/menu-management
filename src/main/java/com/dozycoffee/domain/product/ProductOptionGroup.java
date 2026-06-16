package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;

public class ProductOptionGroup {

    private Long id;
    private long productId;
    private long optionGroupId;
    private boolean isRequired;
    private boolean allowMultiple;
    private Instant createdAt;

    private ProductOptionGroup(Long id, long productId, long optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        validateRequiredFields(createdAt);
        this.id = id;
        this.productId = productId;
        this.optionGroupId = optionGroupId;
        this.isRequired = isRequired;
        this.allowMultiple = allowMultiple;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductOptionGroup that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static ProductOptionGroup of(long id, long productId, long optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        return new ProductOptionGroup(id, productId, optionGroupId, isRequired, allowMultiple, createdAt);
    }

    public static ProductOptionGroup create(long productId, long optionGroupId, boolean isRequired, boolean allowMultiple) {
        return new ProductOptionGroup(null, productId, optionGroupId, isRequired, allowMultiple, Instant.now());
    }

    public static void validateRequiredFields(Instant createdAt) {
        if (createdAt == null) {
            throw new ProductException("createdAt must not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public long getOptionGroupId() {
        return optionGroupId;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public boolean getAllowMultiple() {
        return allowMultiple;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
