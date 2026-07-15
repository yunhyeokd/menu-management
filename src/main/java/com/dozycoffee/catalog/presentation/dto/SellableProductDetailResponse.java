package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.SellableProductDetailResult;
import com.dozycoffee.catalog.application.dto.TagData;

import java.util.List;

public record SellableProductDetailResponse(
        String productId,
        String name,
        String description,
        String imageUrl,
        ProductDetailResponse.CategorySummary category,
        int price,
        Integer kcal,
        String allergenInfo,
        List<String> tags,
        List<ProductDetailResponse.OptionGroupSummary> optionGroups,
        String branchId,
        boolean soldOut
) {

    public static SellableProductDetailResponse from(SellableProductDetailResult result) {
        return new SellableProductDetailResponse(
                result.id().getValue(),
                result.name(),
                result.description(),
                result.imageUrl(),
                result.category() != null
                        ? new ProductDetailResponse.CategorySummary(result.category().id().getValue(), result.category().name())
                        : null,
                result.price(),
                result.kcal(),
                result.allergenInfo() != null ? result.allergenInfo().toString() : null,
                result.tags().stream().map(TagData::name).toList(),
                result.optionGroups().stream().map(ProductDetailResponse.OptionGroupSummary::from).toList(),
                result.branchId() != null ? result.branchId().getValue() : null,
                result.soldOut()
        );
    }
}
