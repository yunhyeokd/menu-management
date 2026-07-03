package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.application.dto.ProductSearchCommand;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductKind;
import com.dozycoffee.product.domain.ProductStatus;

import java.util.List;
import java.util.Optional;

public record ProductSearchRequest(
        String name,
        List<CategoryId> categoryIds,
        List<String> tags,
        List<OptionGroupId> optionGroupIds,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        BranchId branchId
) {

    public ProductSearchCommand toCommand() {
        return new ProductSearchCommand(
                Optional.ofNullable(name),
                categoryIds != null ? categoryIds : List.of(),
                tags != null ? tags : List.of(),
                optionGroupIds != null ? optionGroupIds : List.of(),
                statuses != null ? statuses : List.of(),
                kinds != null ? kinds : List.of(),
                Optional.ofNullable(branchId)
        );
    }
}
