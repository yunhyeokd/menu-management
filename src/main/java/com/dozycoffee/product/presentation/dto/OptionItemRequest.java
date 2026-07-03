package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionItemCreateCommand;

public record OptionItemRequest(
        String name,
        String description,
        int price
) {

    public OptionItemCreateCommand toCommand() {
        return new OptionItemCreateCommand(name, description, price);
    }
}
