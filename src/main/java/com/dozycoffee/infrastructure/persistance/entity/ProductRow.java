package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;

import java.time.Instant;

public record ProductRow(
        String productId,
        String name,
        String description,
        String imageUrl,
        String categoryId,
        int price,
        Integer kcal,
        String allergenInfo,
        String kind,
        String branchId,
        String status,
        Instant createdAt
) {
    public Product toProduct() {
        return Product.of(
                ProductId.of(productId),
                name,
                description,
                imageUrl,
                categoryId != null ? CategoryId.of(categoryId) : null,
                price,
                kcal,
                allergenInfo != null ? AllergenInfo.of(allergenInfo) : null,
                ProductKind.of(kind),
                branchId != null ? BranchId.of(branchId) : null,
                ProductStatus.of(status),
                createdAt
        );
    }
}
