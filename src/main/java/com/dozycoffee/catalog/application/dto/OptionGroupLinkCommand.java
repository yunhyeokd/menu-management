package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.OptionGroupId;

public record OptionGroupLinkCommand(
        OptionGroupId optionGroupId,
        boolean isRequired,
        boolean allowMultiple
) {}
