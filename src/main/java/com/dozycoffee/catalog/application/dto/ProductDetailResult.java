package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;

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
