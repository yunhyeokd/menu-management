package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupData;
import com.dozycoffee.product.application.dto.OptionItemData;

import java.time.Instant;
import java.util.List;

public record OptionGroupResponse(
        String optionGroupId,
        String name,
        String description,
        List<OptionItemResponse> items,
        Instant createdAt
) {

    public static OptionGroupResponse from(OptionGroupData data) {
        return new OptionGroupResponse(
                data.id().getValue(),
                data.name(),
                data.description(),
                data.items().stream().map(OptionItemResponse::from).toList(),
                data.createdAt()
        );
    }

    public record OptionItemResponse(String name, String description, int price) {
        public static OptionItemResponse from(OptionItemData data) {
            return new OptionItemResponse(data.name(), data.description(), data.price());
        }
    }
}
