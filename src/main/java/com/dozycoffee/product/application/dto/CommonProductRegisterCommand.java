package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;

import java.util.List;
import java.util.Set;

public record CommonProductRegisterCommand(
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
