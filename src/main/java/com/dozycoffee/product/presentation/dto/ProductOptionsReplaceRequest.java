package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupLinkCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductOptionsReplaceRequest(
        @NotNull @Valid List<OptionGroupLinkRequest> optionGroups
) {

    public List<OptionGroupLinkCommand> toCommand() {
        return optionGroups.stream().map(OptionGroupLinkRequest::toCommand).toList();
    }

}
