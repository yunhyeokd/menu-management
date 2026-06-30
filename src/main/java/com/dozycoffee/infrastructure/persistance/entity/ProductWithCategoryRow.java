package com.dozycoffee.infrastructure.persistance.entity;

import java.time.Instant;

public record ProductWithCategoryRow(
        String productId,
        String name,
        String description,
        String imageUrl,
        String categoryId,
        String categoryName,
        int price,
        Integer kcal,
        String allergenInfo,
        String kind,
        String branchId,
        String status,
        Instant createdAt
) {}
