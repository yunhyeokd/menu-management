package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupCreateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OptionGroupCreateRequest(
        @NotBlank String name,
        String description,
        @NotEmpty @Valid List<OptionItemRequest> items
) {

    public OptionGroupCreateCommand toCommand() {
        return new OptionGroupCreateCommand(
                name,
                description,
                items.stream().map(OptionItemRequest::toCommand).toList()
        );
    }
}
