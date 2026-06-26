package com.dozycoffee.application.product.dto;

import com.dozycoffee.product.domain.OptionGroupId;

import java.util.List;

public record OptionGroupItemUpdateCommand(
        OptionGroupId optionGroupId,
        List<OptionItemCreateCommand> items
) {
}
