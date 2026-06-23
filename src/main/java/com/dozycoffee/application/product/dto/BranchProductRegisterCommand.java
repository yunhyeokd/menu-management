package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.product.AllergenInfo;
import com.dozycoffee.domain.product.CategoryId;

import java.util.List;
import java.util.Set;

public record BranchProductRegisterCommand(
        BranchId branchId,
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        Set<String> tagNames,
        List<OptionGroupLinkSpec> optionGroups
) {
}
