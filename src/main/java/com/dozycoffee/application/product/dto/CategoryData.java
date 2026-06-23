package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.CategoryId;

public record CategoryData(
        CategoryId id,
        String name
) {
    public static CategoryData from(Category category) {
        return new CategoryData(category.getId(), category.getName());
    }
}
