package com.dozycoffee.application.product.dto;

import com.dozycoffee.product.domain.OptionItem;
import com.dozycoffee.product.domain.OptionItemId;

import java.time.Instant;

public record OptionItemData(
        OptionItemId id,
        String name,
        String description,
        int price,
        Instant createdAt
) {
    public static OptionItemData from(OptionItem optionItem) {
        return new OptionItemData(
                optionItem.getId(),
                optionItem.getName(),
                optionItem.getDescription(),
                optionItem.getPrice(),
                optionItem.getCreatedAt()
        );
    }
}
