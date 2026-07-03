package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupItemUpdateCommand;

import java.util.List;

public record OptionGroupItemsUpdateRequest(
        List<OptionItemRequest> items
) {

    public OptionGroupItemUpdateCommand toCommand() {
        return new OptionGroupItemUpdateCommand(
                items.stream().map(OptionItemRequest::toCommand).toList()
        );
    }
}
