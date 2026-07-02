package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.model.BranchProduct;

import java.util.List;

public record BranchProductSummaryResponse(
        String product_id,
        String branch_id,
        boolean active,
        String name,
        int price,
        String image_url,
        List<String> tags,
        String category_id
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
