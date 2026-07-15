package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.*;

import java.util.List;

public record ProductFilterQuery(
        String name,
        List<CategoryId> categoryIds,
        List<TagId> tagIds,
        List<OptionGroupId> optionGroupIds,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        BranchId branchId
) {
    public static ProductFilterQuery empty() {
        return new ProductFilterQuery(
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null
        );
    }
}
