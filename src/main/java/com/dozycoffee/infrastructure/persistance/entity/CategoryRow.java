package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;

import java.time.Instant;

public record CategoryRow(
        String categoryId,
        String name,
        Instant createdAt
) {
    public Category toCategory() {
        return Category.of(CategoryId.of(categoryId), name, createdAt);
    }
}
