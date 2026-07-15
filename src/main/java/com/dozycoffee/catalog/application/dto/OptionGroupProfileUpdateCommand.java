package com.dozycoffee.catalog.application.dto;

import java.util.Optional;

public record OptionGroupProfileUpdateCommand(
        String name,
        Optional<String> description
) {
}
