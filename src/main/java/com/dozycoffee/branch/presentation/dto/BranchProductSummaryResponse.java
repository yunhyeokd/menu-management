package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.model.BranchProduct;

import java.util.List;

public record BranchProductSummaryResponse(
        String productId,
        String branchId,
        boolean active,
        String name,
        int price,
        String imageUrl,
        List<String> tags,
        String categoryId
) {

    public static BranchProductSummaryResponse from(BranchProduct branchProduct) {
        return new BranchProductSummaryResponse(
                branchProduct.getProductId().getValue(),
                branchProduct.getBranchId() != null ? branchProduct.getBranchId().getValue() : null,
                branchProduct.isActive(),
                branchProduct.getName(),
                branchProduct.getPrice(),
                branchProduct.getImageUrl(),
                branchProduct.getTags(),
                branchProduct.getCategoryId() != null ? branchProduct.getCategoryId().getValue() : null
        );
    }
}
