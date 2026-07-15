package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.Category;
import com.dozycoffee.catalog.domain.CategoryId;

public record CategoryData(
        CategoryId id,
        String name
) {
    public static CategoryData from(Category category) {
        return new CategoryData(category.getId(), category.getName());
    }

    public static CategoryData from(CategoryId categoryId) {
        return new CategoryData(categoryId, null);
    }
}
