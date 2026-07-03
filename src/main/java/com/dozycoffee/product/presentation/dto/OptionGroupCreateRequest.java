package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupCreateCommand;

import java.util.List;

public record OptionGroupCreateRequest(
        String name,
        String description,
        List<OptionItemRequest> items
) {

    public OptionGroupCreateCommand toCommand() {
        return new OptionGroupCreateCommand(
                name,
                description,
                items.stream().map(OptionItemRequest::toCommand).toList()
        );
    }
}
