package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.CategoryData;

public record CategoryResponse(
        String id,
        String name
) {
    public static CategoryResponse from(CategoryData categoryData) {
        return new CategoryResponse(categoryData.id().getValue(), categoryData.name());
    }
}
