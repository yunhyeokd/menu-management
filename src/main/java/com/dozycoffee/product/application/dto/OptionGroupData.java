package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.OptionGroup;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.OptionItem;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record OptionGroupData(
        OptionGroupId id,
        String name,
        String description,
        List<OptionItemData> items,
        Instant createdAt
) {
    public static OptionGroupData from(OptionGroup optionGroup) {
        return new OptionGroupData(
                optionGroup.getId(),
                optionGroup.getName(),
                optionGroup.getDescription(),
                optionGroup.getItems().stream().map(OptionItemData::from).toList(),
                optionGroup.getCreatedAt()
        );
    }
}
