package com.dozycoffee.application.product.dto;

import com.dozycoffee.product.domain.OptionGroupId;

import java.util.Optional;

public record OptionGroupProfileUpdateCommand(
        OptionGroupId optionGroupId,
        String name,
        Optional<String> description
) {
}
