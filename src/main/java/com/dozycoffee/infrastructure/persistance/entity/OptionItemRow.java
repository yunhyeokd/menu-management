package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.catalog.domain.OptionItem;

import java.time.Instant;

public record OptionItemRow(
        String optionGroupId,
        String name,
        String description,
        int price,
        Instant createdAt
) {
    public OptionItem toOptionItem() {
        return OptionItem.of(name, description, price, createdAt);
    }
}
