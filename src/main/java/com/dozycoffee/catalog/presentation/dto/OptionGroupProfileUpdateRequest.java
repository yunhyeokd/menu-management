package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.OptionGroupProfileUpdateCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record OptionGroupProfileUpdateRequest(
        @NotBlank String name,
        String description
) {

    public OptionGroupProfileUpdateCommand toCommand() {
        return new OptionGroupProfileUpdateCommand(name, Optional.ofNullable(description));
    }
}
