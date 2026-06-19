package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.product.ProductId;

import java.time.Instant;
import java.util.Objects;

public class ProductSalesOverride {

    private Long id;
    private ProductId productId;
    private BranchId branchId;
    private ProductSalesOverrideStatus status;
    private Instant createdAt;

    private ProductSalesOverride(Long id, ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        setProductId(productId);
        setBranchId(branchId);
        setStatus(status);
        setCreatedAt(createdAt);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductSalesOverride that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static ProductSalesOverride of(long id, ProductId productId, BranchId branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        return new ProductSalesOverride(id, productId, branchId, status, createdAt);
    }

    public static ProductSalesOverride create(ProductId productId, BranchId branchId, ProductSalesOverrideStatus status) {
        return new ProductSalesOverride(null, productId, branchId, status, Instant.now());
    }

    public Long getId() {
        return id;
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
