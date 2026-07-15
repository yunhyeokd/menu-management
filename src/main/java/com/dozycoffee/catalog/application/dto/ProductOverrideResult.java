package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;

import java.util.List;

public record ProductOverrideResult(
        ProductId id,
        String name,
        int price,
        String imageUrl,
        CategoryData category,
        List<TagData> tags,
        BranchId branchId,
        ProductSalesOverrideStatus overrideStatus
) {
    public static ProductOverrideResult from(ProductSnapshot product, ProductSalesOverrideStatus overrideStatus) {
        return new ProductOverrideResult(
                product.id(),
                product.name(),
                product.price(),
                product.imageUrl(),
                product.categoryId() != null ? CategoryData.from(product.categoryId()) : null,
                product.tags(),
                product.branchId(),
                overrideStatus
        );
    }
}
