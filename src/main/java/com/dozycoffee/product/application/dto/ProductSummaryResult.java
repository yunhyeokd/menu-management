package com.dozycoffee.product.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductKind;
import com.dozycoffee.product.domain.ProductStatus;

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
