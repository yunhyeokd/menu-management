package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.OptionGroup;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.OptionItem;

import java.time.Instant;
import java.util.List;

public record OptionGroupData(
        OptionGroupId id,
        String name,
        String description,
        List<OptionItemData> items,
        Instant createdAt
) {
    public static OptionGroupData from(OptionGroup optionGroup, List<OptionItem> optionItems) {
        return new OptionGroupData(
                optionGroup.getId(),
                optionGroup.getName(),
                optionGroup.getDescription(),
                optionItems.stream().map(OptionItemData::from).toList(),
                optionGroup.getCreatedAt()
        );
    }
}
