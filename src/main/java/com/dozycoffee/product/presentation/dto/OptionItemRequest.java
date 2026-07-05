package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionItemCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record OptionItemRequest(
        @NotBlank String name,
        String description,
        @PositiveOrZero int price
) {

    public OptionItemCreateCommand toCommand() {
        return new OptionItemCreateCommand(name, description, price);
    }
}
