package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupProfileUpdateCommand;

import java.util.Optional;

public record OptionGroupProfileUpdateRequest(
        String name,
        String description
) {

    public OptionGroupProfileUpdateCommand toCommand() {
        return new OptionGroupProfileUpdateCommand(name, Optional.ofNullable(description));
    }
}
