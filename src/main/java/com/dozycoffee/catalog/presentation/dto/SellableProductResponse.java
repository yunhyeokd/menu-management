package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.SellableProductResult;
import com.dozycoffee.catalog.application.dto.TagData;

import java.util.List;

public record SellableProductResponse(
        String productId,
        String name,
        int price,
        String imageUrl,
        String categoryId,
        String categoryName,
        List<String> tags,
        String branchId,
        boolean soldOut
) {

    public static SellableProductResponse from(SellableProductResult result) {
        return new SellableProductResponse(
                result.id().getValue(),
                result.name(),
                result.price(),
                result.imageUrl(),
                result.category() != null ? result.category().id().getValue() : null,
                result.category() != null ? result.category().name() : null,
                result.tags().stream().map(TagData::name).toList(),
                result.branchId() != null ? result.branchId().getValue() : null,
                result.soldOut()
        );
    }
}
