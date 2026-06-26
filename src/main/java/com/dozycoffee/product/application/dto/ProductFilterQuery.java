package com.dozycoffee.application.product.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;

import java.util.List;
import java.util.Optional;

public record ProductFilterQuery(
        Optional<String> name,
        Optional<CategoryId> categoryId,
        List<TagId> tagIds,
        Optional<OptionGroupId> optionGroupId,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        Optional<BranchId> branchId
) {
    public static ProductFilterQuery empty() {
        return new ProductFilterQuery(
                Optional.empty(),
                Optional.empty(),
                List.of(),
                Optional.empty(),
                List.of(),
                List.of(),
                Optional.empty()
        );
    }
}
