package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.OptionItem;

import java.time.Instant;

public record OptionItemData(
        String name,
        String description,
        int price,
        Instant createdAt
) {
    public static OptionItemData from(OptionItem optionItem) {
        return new OptionItemData(
                optionItem.getName(),
                optionItem.getDescription(),
                optionItem.getPrice(),
                optionItem.getCreatedAt()
        );
    }
}
