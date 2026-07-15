package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;

import java.util.List;

public record ProductSummaryResult(
        ProductId id,
        String name,
        String imageUrl,
        CategoryData category,
        int price,
        ProductKind kind,
        BranchId branchId,
        ProductStatus status,
        List<TagData> tags
) {
    public static ProductSummaryResult from(ProductSnapshot product, List<TagData> tags) {
        return new ProductSummaryResult(
                product.id(),
                product.name(),
                product.imageUrl(),
                product.categoryId() != null ? CategoryData.from(product.categoryId()) : null,
                product.price(),
                product.kind(),
                product.branchId(),
                product.status(),
                tags
        );
    }
}
