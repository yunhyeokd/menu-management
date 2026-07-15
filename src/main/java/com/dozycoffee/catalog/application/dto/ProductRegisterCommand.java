package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductKind;

import java.util.List;
import java.util.Set;

public record ProductRegisterCommand(
        ProductKind kind,
        BranchId branchId,
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        Set<String> tagNames,
        List<OptionGroupLinkCommand> optionGroups
) {
}
