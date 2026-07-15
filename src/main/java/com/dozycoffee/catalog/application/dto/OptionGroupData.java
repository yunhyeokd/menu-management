package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.OptionGroup;
import com.dozycoffee.catalog.domain.OptionGroupId;
import com.dozycoffee.catalog.domain.OptionItem;

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
