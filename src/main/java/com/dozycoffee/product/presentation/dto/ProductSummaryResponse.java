package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.ProductSummaryResult;
import com.dozycoffee.product.application.dto.TagData;

import java.util.List;

public record ProductSummaryResponse(
        String productId,
        String name,
        String imageUrl,
        String categoryId,
        String categoryName,
        int price,
        String kind,
        String branchId,
        String status,
        List<String> tags
) {

    public static ProductSummaryResponse from(ProductSummaryResult result) {
        return new ProductSummaryResponse(
                result.id().getValue(),
                result.name(),
                result.imageUrl(),
                result.category() != null ? result.category().id().getValue() : null,
                result.category() != null ? result.category().name() : null,
                result.price(),
                result.kind().name(),
                result.branchId() != null ? result.branchId().getValue() : null,
                result.status().name(),
                result.tags().stream().map(TagData::name).toList()
        );
    }

}
