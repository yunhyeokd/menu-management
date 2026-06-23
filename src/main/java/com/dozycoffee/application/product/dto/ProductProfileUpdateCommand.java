package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.AllergenInfo;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.ProductId;

import java.util.Set;

public record ProductProfileUpdateCommand(
        ProductId id,
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        Set<String> tags
) {
}
