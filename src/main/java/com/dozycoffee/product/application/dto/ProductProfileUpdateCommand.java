package com.dozycoffee.application.product.dto;

import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductId;

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
