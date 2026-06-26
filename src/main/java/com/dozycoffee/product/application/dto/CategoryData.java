package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;

public record CategoryData(
        CategoryId id,
        String name
) {
    public static CategoryData from(Category category) {
        return new CategoryData(category.getId(), category.getName());
    }
}
