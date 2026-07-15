package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.*;

import java.time.Instant;
import java.util.List;

public record ProductSnapshot(
        ProductId id,
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        ProductKind kind,
        BranchId branchId,
        ProductStatus status,
        List<TagData> tags,
        Instant createdAt
) {
    public static ProductSnapshot from(Product product, List<TagData> tags) {
        return new ProductSnapshot(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCategoryId(),
                product.getPrice(),
                product.getKcal(),
                product.getAllergenInfo(),
                product.getKind(),
                product.getBranchId(),
                product.getStatus(),
                tags,
                product.getCreatedAt()
        );
    }
}
