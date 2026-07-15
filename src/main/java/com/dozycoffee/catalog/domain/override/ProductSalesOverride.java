package com.dozycoffee.catalog.domain.override;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductException;
import com.dozycoffee.catalog.domain.ProductId;

import java.time.Instant;
import java.util.Objects;

public class ProductSalesOverride {

    private final ProductId productId;
    private final BranchId branchId;
    private ProductSalesOverrideStatus status;
    private final Instant createdAt;

    private ProductSalesOverride(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        if (productId == null) throw new ProductException("productId cannot be null");
        if (branchId == null) throw new ProductException("branchId cannot be null");
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
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
        if (status == null) throw new ProductException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void updateStatus(ProductSalesOverrideStatus status) {
        setStatus(status);
    }
}
