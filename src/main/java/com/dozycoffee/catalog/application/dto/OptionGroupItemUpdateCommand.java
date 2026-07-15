package com.dozycoffee.catalog.application.dto;

import java.util.List;

public record OptionGroupItemUpdateCommand(
        List<OptionItemCreateCommand> items
) {
}
