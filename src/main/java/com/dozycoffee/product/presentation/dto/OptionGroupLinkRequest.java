package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupLinkCommand;
import com.dozycoffee.product.domain.OptionGroupId;
import jakarta.validation.constraints.NotNull;

public record OptionGroupLinkRequest(
        @NotNull String optionGroupId,
        @NotNull Boolean isRequired,
        @NotNull Boolean allowMultiple
) {

    public OptionGroupLinkCommand toCommand() {
        return new OptionGroupLinkCommand(OptionGroupId.of(optionGroupId), isRequired, allowMultiple);
    }

}
