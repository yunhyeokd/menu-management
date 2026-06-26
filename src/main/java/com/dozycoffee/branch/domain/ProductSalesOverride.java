package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.product.ProductId;

import java.time.Instant;
import java.util.Objects;

public class ProductSalesOverride {

    private ProductId productId;
    private BranchId branchId;
    private ProductSalesOverrideStatus status;
    private Instant createdAt;

    private ProductSalesOverride(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        setProductId(productId);
        setBranchId(branchId);
        setStatus(status);
        setCreatedAt(createdAt);
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

    private void setProductId(ProductId productId) {
        if (productId == null) throw new BranchException("productId cannot be null");
        this.productId = productId;
    }

    public BranchId getBranchId() {
        return branchId;
    }

    private void setBranchId(BranchId branchId) {
        if (branchId == null) throw new BranchException("branchId cannot be null");
        this.branchId = branchId;
    }

    public ProductSalesOverrideStatus getStatus() {
        return status;
    }

    public void updateStatus(ProductSalesOverrideStatus status) {
        setStatus(status);
    }

    private void setStatus(ProductSalesOverrideStatus status) {
        if (status == null) throw new BranchException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new BranchException("createdAt cannot be null");
        this.createdAt = createdAt;
    }
}
