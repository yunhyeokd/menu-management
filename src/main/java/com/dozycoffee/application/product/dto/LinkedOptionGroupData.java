package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;
import com.dozycoffee.domain.product.ProductOptionGroupId;

import java.util.List;

public record LinkedOptionGroupData(
        ProductOptionGroupId linkId,
        OptionGroupId id,
        String name,
        String description,
        boolean isRequired,
        boolean allowMultiple,
        List<OptionItemData> items
) {}
