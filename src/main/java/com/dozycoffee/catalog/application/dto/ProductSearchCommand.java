package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.*;

import java.util.List;

public record ProductSearchCommand(
        String name,
        List<CategoryId> categoryIds,
        List<String> tagNames,
        List<OptionGroupId> optionGroupIds,
        List<ProductStatus> statuses,
        List<ProductKind> kinds,
        BranchId branchId
) {
}
