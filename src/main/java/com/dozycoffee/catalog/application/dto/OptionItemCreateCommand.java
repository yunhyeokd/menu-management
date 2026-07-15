package com.dozycoffee.catalog.application.dto;

import java.util.Optional;

public record OptionItemCreateCommand(
        String name,
        String description,
        int price
) {
}
