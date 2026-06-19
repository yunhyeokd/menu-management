package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;

public class ProductOptionGroup {

    private Long id;
    private ProductId productId;
    private OptionGroupId optionGroupId;
    private boolean isRequired;
    private boolean allowMultiple;
    private Instant createdAt;

    private ProductOptionGroup(Long id, ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        setProductId(productId);
        setOptionGroupId(optionGroupId);
        setCreatedAt(createdAt);
        this.id = id;
        this.isRequired = isRequired;
        this.allowMultiple = allowMultiple;
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

    public static ProductOptionGroup of(long id, ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple, Instant createdAt) {
        return new ProductOptionGroup(id, productId, optionGroupId, isRequired, allowMultiple, createdAt);
    }

    public static ProductOptionGroup create(ProductId productId, OptionGroupId optionGroupId, boolean isRequired, boolean allowMultiple) {
        return new ProductOptionGroup(null, productId, optionGroupId, isRequired, allowMultiple, Instant.now());
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

    public OptionGroupId getOptionGroupId() {
        return optionGroupId;
    }

    private void setOptionGroupId(OptionGroupId optionGroupId) {
        if (optionGroupId == null) throw new ProductException("optionGroupId cannot be null");
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
