package com.dozycoffee.product.application.dto;

import java.util.List;

public record OptionGroupItemUpdateCommand(
        List<OptionItemCreateCommand> items
) {
}
