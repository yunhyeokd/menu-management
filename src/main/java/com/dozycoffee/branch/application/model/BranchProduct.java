package com.dozycoffee.branch.application.model;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.ProductId;

import java.util.Objects;

public class BranchProduct {

    private final ProductId productId;
    private final BranchId branchId;
    private final boolean active;

    private BranchProduct(ProductId productId, BranchId branchId, boolean active) {
        this.productId = productId;
        this.branchId = branchId;
        this.active = active;
    }

    public static BranchProduct of(ProductId productId, BranchId branchId, boolean active) {
        return new BranchProduct(productId, branchId, active);
    }

    public ProductId getProductId() {
        return productId;
    }

    public BranchId getBranchId() {
        return branchId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCommon() {
        return branchId == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BranchProduct that)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}
