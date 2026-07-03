package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.OptionGroupLinkCommand;

import java.util.List;

public record ProductOptionsReplaceRequest(
        List<OptionGroupLinkRequest> optionGroups
) {

    public List<OptionGroupLinkCommand> toCommand() {
        return optionGroups.stream().map(OptionGroupLinkRequest::toCommand).toList();
    }

}
