package com.dozycoffee.product.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;

import java.util.List;
import java.util.Optional;

public record ProductSearchCommand(
        Optional<String> name,
        List<CategoryId> categoryIds,
        List<String> tagNames,
        List<OptionGroupId> optionGroupIds,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        Optional<BranchId> branchId
) {
}
