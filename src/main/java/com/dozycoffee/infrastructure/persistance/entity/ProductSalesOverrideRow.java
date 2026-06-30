package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.domain.ProductSalesOverride;
import com.dozycoffee.branch.domain.ProductSalesOverrideStatus;
import com.dozycoffee.product.domain.ProductId;

import java.time.Instant;

public record ProductSalesOverrideRow(
        String productId,
        String branchId,
        String status,
        Instant createdAt
) {
    public ProductSalesOverride toProductSalesOverride() {
        return ProductSalesOverride.of(
                ProductId.of(productId),
                BranchId.of(branchId),
                ProductSalesOverrideStatus.of(status),
                createdAt
        );
    }
}
