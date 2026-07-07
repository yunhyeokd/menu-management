package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.application.dto.ProductSearchCommand;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductKind;
import com.dozycoffee.product.domain.ProductStatus;

import java.util.List;

public record ProductSearchRequest(
        String name,
        List<String> categoryIds,
        List<String> tags,
        List<String> optionGroupIds,
        List<String> statuses,
        List<String> kinds,
        String branchId
) {

    public ProductSearchCommand toCommand() {
        return new ProductSearchCommand(
                name,
                categoryIds != null ? categoryIds.stream().map(CategoryId::of).toList() : List.of(),
                tags != null ? tags : List.of(),
                optionGroupIds != null ? optionGroupIds.stream().map(OptionGroupId::of).toList() : List.of(),
                statuses != null ? statuses.stream().map(ProductStatus::of).toList() : List.of(),
                kinds != null ? kinds.stream().map(ProductKind::of).toList() : List.of(),
                branchId != null ? BranchId.of(branchId) : null
        );
    }
}
