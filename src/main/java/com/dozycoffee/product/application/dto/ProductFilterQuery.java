package com.dozycoffee.product.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;

import java.util.List;
import java.util.Optional;

public record ProductFilterQuery(
        Optional<String> name,
        List<CategoryId> categoryIds,
        List<TagId> tagIds,
        List<OptionGroupId> optionGroupIds,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        Optional<BranchId> branchId
) {
    public static ProductFilterQuery empty() {
        return new ProductFilterQuery(
                Optional.empty(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                Optional.empty()
        );
    }
}
