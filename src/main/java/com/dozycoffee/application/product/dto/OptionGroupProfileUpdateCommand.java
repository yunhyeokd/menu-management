package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;

import java.util.Optional;

public record OptionGroupProfileUpdateCommand(
        OptionGroupId optionGroupId,
        String name,
        Optional<String> description
) {
}
