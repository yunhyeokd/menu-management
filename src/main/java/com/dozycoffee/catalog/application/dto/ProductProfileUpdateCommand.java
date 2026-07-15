package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.Set;

public record ProductProfileUpdateCommand(
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
