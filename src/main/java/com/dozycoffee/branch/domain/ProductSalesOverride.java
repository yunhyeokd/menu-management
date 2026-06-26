package com.dozycoffee.branch.domain;

import com.dozycoffee.product.domain.ProductId;

import java.time.Instant;
import java.util.Objects;

public class ProductSalesOverride {

    private final ProductId productId;
    private final BranchId branchId;
    private ProductSalesOverrideStatus status;
    private final Instant createdAt;

    private ProductSalesOverride(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        if (productId == null) throw new BranchException("productId cannot be null");
        if (branchId == null) throw new BranchException("branchId cannot be null");
        if (createdAt == null) throw new BranchException("createdAt cannot be null");
        this.productId = productId;
        this.branchId = branchId;
        setStatus(status);
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductSalesOverride that)) return false;
        return Objects.equals(productId, that.productId) && Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, branchId);
    }

    public static ProductSalesOverride of(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        return new ProductSalesOverride(productId, branchId, status, createdAt);
    }

    public static ProductSalesOverride create(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status) {
        return new ProductSalesOverride(productId, branchId, status, Instant.now());
    }

    public ProductId getProductId() {
        return productId;
    }

    public BranchId getBranchId() {
        return branchId;
    }

    public ProductSalesOverrideStatus getStatus() {
        return status;
    }

    private void setStatus(ProductSalesOverrideStatus status) {
        if (status == null) throw new BranchException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void updateStatus(ProductSalesOverrideStatus status) {
        setStatus(status);
    }
}
