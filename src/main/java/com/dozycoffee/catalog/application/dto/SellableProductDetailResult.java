package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;

public record SellableProductDetailResult(
        ProductId id,
        String name,
        String description,
        String imageUrl,
        CategoryData category,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        List<TagData> tags,
        List<LinkedOptionGroupData> optionGroups,
        BranchId branchId,
        boolean soldOut
) {
    public static SellableProductDetailResult from(ProductDetailResult detail, boolean soldOut) {
        return new SellableProductDetailResult(
                detail.id(),
                detail.name(),
                detail.description(),
                detail.imageUrl(),
                detail.category(),
                detail.price(),
                detail.kcal(),
                detail.allergenInfo(),
                detail.tags(),
                detail.optionGroups(),
                detail.branchId(),
                soldOut
        );
    }
}
