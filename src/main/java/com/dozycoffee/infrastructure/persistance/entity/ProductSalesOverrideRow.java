package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;

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
