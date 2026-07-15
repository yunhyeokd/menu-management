package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.OptionGroupItemUpdateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OptionGroupItemsUpdateRequest(
        @NotEmpty @Valid List<OptionItemRequest> items
) {

    public OptionGroupItemUpdateCommand toCommand() {
        return new OptionGroupItemUpdateCommand(
                items.stream().map(OptionItemRequest::toCommand).toList()
        );
    }
}
