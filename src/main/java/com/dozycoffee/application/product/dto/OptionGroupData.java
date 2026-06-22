package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;

import java.time.Instant;
import java.util.List;

public record OptionGroupData(
        OptionGroupId id,
        String name,
        String description,
        List<OptionItemData> items,
        Instant createdAt
) {
}
