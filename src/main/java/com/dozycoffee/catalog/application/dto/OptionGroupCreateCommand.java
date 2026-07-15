package com.dozycoffee.catalog.application.dto;

import java.util.List;
import java.util.Optional;

public record OptionGroupCreateCommand(
        String name,
        String description,
        List<OptionItemCreateCommand> items
) {
}
