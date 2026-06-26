package com.dozycoffee.product.application.dto;

import java.util.Optional;

public record OptionItemCreateCommand(
        String name,
        Optional<String> description,
        int price
) {
}
