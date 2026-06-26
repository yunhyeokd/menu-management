package com.dozycoffee.product.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;

import java.time.Instant;
import java.util.List;

public record ProductData(
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
    public static ProductData from(Product product, List<TagData> tags) {
        return new ProductData(
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
