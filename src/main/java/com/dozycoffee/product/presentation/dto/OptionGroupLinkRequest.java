package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupLinkCommand;
import com.dozycoffee.product.domain.OptionGroupId;

public record OptionGroupLinkRequest(
        OptionGroupId optionGroupId,
        Boolean isRequired,
        Boolean allowMultiple
) {

    public OptionGroupLinkCommand toCommand() {
        return new OptionGroupLinkCommand(optionGroupId, isRequired, allowMultiple);
    }

}
