package com.dozycoffee.product.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductKind;

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
