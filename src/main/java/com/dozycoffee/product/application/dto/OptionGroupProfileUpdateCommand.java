package com.dozycoffee.product.application.dto;

import java.util.Optional;

public record OptionGroupProfileUpdateCommand(
        String name,
        Optional<String> description
) {
}
