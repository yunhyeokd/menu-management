package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;
import com.dozycoffee.domain.product.ProductId;

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
