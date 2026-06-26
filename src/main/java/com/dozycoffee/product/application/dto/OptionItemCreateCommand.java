package com.dozycoffee.application.product.dto;

import java.util.Optional;

public record OptionItemCreateCommand(
        String name,
        Optional<String> description,
        int price
) {
}
