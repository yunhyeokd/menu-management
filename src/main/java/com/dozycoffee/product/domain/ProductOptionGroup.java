package com.dozycoffee.product.domain;

import java.time.Instant;
import java.util.Objects;

public class ProductOptionGroup {

    private ProductId productId;
    private OptionGroupId optionGroupId;
    private boolean isRequired;
    private boolean allowMultiple;
    private Instant createdAt;

    private ProductOptionGroup(ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        setProductId(productId);
        setOptionGroupId(optionGroupId);
        setCreatedAt(createdAt);
        this.isRequired = isRequired;
        this.allowMultiple = allowMultiple;
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

    private void setProductId(ProductId productId) {
        if (productId == null) throw new ProductException("productId cannot be null");
        this.productId = productId;
    }

    public OptionGroupId getOptionGroupId() {
        return optionGroupId;
    }

    private void setOptionGroupId(OptionGroupId optionGroupId) {
        if (optionGroupId == null) throw new ProductException("option group id cannot be null");
        this.optionGroupId = optionGroupId;
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

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.createdAt = createdAt;
    }
}
