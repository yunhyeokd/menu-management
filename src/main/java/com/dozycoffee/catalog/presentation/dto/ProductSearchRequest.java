package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.dto.ProductSearchCommand;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;

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
