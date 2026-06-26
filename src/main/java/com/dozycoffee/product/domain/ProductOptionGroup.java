package com.dozycoffee.product.domain;

import java.time.Instant;
import java.util.Objects;

public class ProductOptionGroup {

    private final ProductId productId;
    private final OptionGroupId optionGroupId;
    private final boolean isRequired;
    private final boolean allowMultiple;
    private final Instant createdAt;

    private ProductOptionGroup(ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        if (productId == null) throw new ProductException("productId cannot be null");
        if (optionGroupId == null) throw new ProductException("optionGroupId cannot be null");
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.productId = productId;
        this.optionGroupId = optionGroupId;
        this.isRequired = isRequired;
        this.allowMultiple = allowMultiple;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductOptionGroup that)) return false;
        return Objects.equals(productId, that.productId) && Objects.equals(optionGroupId, that.optionGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, optionGroupId);
    }

    public static ProductOptionGroup of(ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        return new ProductOptionGroup(productId, optionGroupId, isRequired, allowMultiple, createdAt);
    }

    public static ProductOptionGroup create(ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple) {
        return new ProductOptionGroup(productId, optionGroupId, isRequired, allowMultiple, Instant.now());
    }

    public ProductId getProductId() {
        return productId;
    }

    public OptionGroupId getOptionGroupId() {
        return optionGroupId;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
