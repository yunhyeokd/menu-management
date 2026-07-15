package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;

public record LinkedOptionGroupData(
        ProductId productId,
        OptionGroupId optionGroupId,
        String name,
        String description,
        boolean isRequired,
        boolean allowMultiple,
        List<OptionItemData> items
) {}
