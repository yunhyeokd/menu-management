package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.product.*;

import java.time.Instant;
import java.util.List;

public record ProductDetailResult(
        ProductId id,
        String name,
        String description,
        String imageUrl,
        CategoryData category,
        int price,
        Integer kcal,
        AllergenInfo allergenInfo,
        ProductKind kind,
        BranchId branchId,
        ProductStatus status,
        List<TagData> tags,
        List<LinkedOptionGroupData> optionGroups,
        Instant createdAt
) {}
