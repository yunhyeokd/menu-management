package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;

import java.util.List;

public record OptionGroupItemUpdateCommand(
        OptionGroupId optionGroupId,
        List<OptionItemCreateCommand> items
) {
}
