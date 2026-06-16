package com.dozycoffee.domain.branch;

import java.time.Instant;
import java.util.Objects;

public class ProductSalesOverride {

    private Long id;
    private long productId;
    private long branchId;
    private ProductSalesOverrideStatus status;
    private Instant createdAt;

    private ProductSalesOverride(Long id, long productId, long branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        validateRequiredFields(status);
        this.id = id;
        this.productId = productId;
        this.branchId = branchId;
        this.status = status;
        this.createdAt = createdAt;
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

    public static ProductSalesOverride of(long id, long productId, long branchId, ProductSalesOverrideStatus status, Instant createdAt) {
        return new ProductSalesOverride(id, productId, branchId, status, createdAt);
    }

    public static ProductSalesOverride create(long productId, long branchId, ProductSalesOverrideStatus status) {
        return new ProductSalesOverride(null, productId, branchId, status, Instant.now());
    }

    private static void validateRequiredFields(ProductSalesOverrideStatus status) {
        if (status == null) {
            throw new BranchException("ProductSalesOverride status must not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public long getBranchId() {
        return branchId;
    }

    public ProductSalesOverrideStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
