package com.dozycoffee.application.product.dto;

import java.util.List;
import java.util.Optional;

public record OptionGroupCreateCommand(
        String name,
        Optional<String> description,
        List<OptionItemCreateCommand> items
) {
}
