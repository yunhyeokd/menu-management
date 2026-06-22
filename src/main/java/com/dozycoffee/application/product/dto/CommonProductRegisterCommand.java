package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.product.AllergenInfo;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.ProductKind;

import java.util.Optional;
import java.util.Set;

public record CommonProductRegisterCommand(
        String name,
        Optional<String> description,
        Optional<String> imageUrl,
        CategoryId categoryId,
        int price,
        Optional<Integer> kcal,
        Optional<AllergenInfo> allergenInfo,
        Set<String> tagNames
) {
}
