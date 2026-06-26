package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;

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
