package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;

public record SellableProductResult(
        ProductId id,
        String name,
        int price,
        String imageUrl,
        CategoryData category,
        List<TagData> tags,
        BranchId branchId,
        boolean soldOut
) {
    public static SellableProductResult from(ProductSnapshot product, boolean soldOut) {
        return new SellableProductResult(
                product.id(),
                product.name(),
                product.price(),
                product.imageUrl(),
                product.categoryId() != null ? CategoryData.from(product.categoryId()) : null,
                product.tags(),
                product.branchId(),
                soldOut
        );
    }
}
